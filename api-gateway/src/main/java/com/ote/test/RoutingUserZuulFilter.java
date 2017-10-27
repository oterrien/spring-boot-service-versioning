package com.ote.test;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

@Component
@Slf4j
public class RoutingUserZuulFilter extends ZuulFilter {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClientRouteLocator routeLocator;

    private static final String DYNAMIC = "DYNAMIC";

    /**
     * - pre filters are executed before the request is routed,
     * - route filters can handle the actual routing of the request,
     * - post filters are executed after the request has been routed,
     * - error filters execute if an error occurs in the course of handling the request.
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * Must be set after PreDecorationFilter
     */
    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String requestUri = URI.create(request.getRequestURI()).getPath();
        String contextPath = requestUri.substring(0, requestUri.indexOf("/", 1));
        Optional.ofNullable(routeLocator.getMatchingRoute(contextPath)).
                filter(matchingRoute -> matchingRoute.getLocation().startsWith(DYNAMIC)).
                ifPresent(matchingRoute -> findRoute(requestContext, contextPath.replace("/", ""), getUserFromHeader(request), matchingRoute.getLocation()));

        return null;
    }

    private String getUserFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION)).
                map(authorization -> {
                    if (authorization.startsWith("Basic")) {
                        String credentialsBase64 = authorization.replace("Basic", "").trim();
                        String credentials = new String(Base64.decode(credentialsBase64));
                        return credentials.substring(0, credentials.indexOf(":"));
                    }
                    return null;
                }).
                orElse(null);
    }

    private void findRoute(RequestContext requestContext, String contextPath, String user, String matchingRoute) {

        try {
            URL url = new URL(matchingRoute.split("\\|")[1]);

            if (user != null) {
                String versionFromUserService = getVersionFromUserService(contextPath, user);
                if (versionFromUserService != null) {
                    String urlFromRoutingConfigurationService = getUrlFromRoutingConfigurationService(contextPath, versionFromUserService);
                    if (urlFromRoutingConfigurationService != null) {
                        log.info("For contextPath " + contextPath + ", redirect User " + user + " to " + urlFromRoutingConfigurationService);
                        url = new URL(urlFromRoutingConfigurationService);
                    }
                }
            }
            requestContext.setRouteHost(url);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            requestContext.unset();
            requestContext.getResponse().setContentType(MediaType.TEXT_HTML_VALUE);
            requestContext.setResponseStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            requestContext.setResponseBody(e.getMessage());
            requestContext.setSendZuulResponse(false);
        }
    }

    /**
     * Call UserService to find a configuration for the given contextPath and the user
     *
     * @param contextPath
     * @param user
     * @return
     */
    private String getVersionFromUserService(String contextPath, String user) {
        Route userServiceRoute = routeLocator.getMatchingRoute("/users");
        String userServiceLocation = userServiceRoute.getLocation();
        URI userServiceUri = UriComponentsBuilder.fromHttpUrl(userServiceLocation).
                path("/api/v1/routes/version").
                queryParam("contextPath", contextPath).
                queryParam("user", user).
                build().encode().toUri();
        ResponseEntity<String> versionToUse = restTemplate.getForEntity(userServiceUri, String.class);
        return versionToUse.getBody();
    }

    private String getUrlFromRoutingConfigurationService(String contextPath, String version) throws Exception {

        Route routingConfigurationServiceRoute = routeLocator.getMatchingRoute("/routes");
        String routingConfigurationServiceLocation = routingConfigurationServiceRoute.getLocation();
        URI routingConfigurationServiceUri = UriComponentsBuilder.fromHttpUrl(routingConfigurationServiceLocation).
                path("/api/v1/routes/location").
                queryParam("contextPath", contextPath).
                queryParam("version", version).
                build().encode().toUri();
        ResponseEntity<String> versionToUse = restTemplate.getForEntity(routingConfigurationServiceUri, String.class);
        return versionToUse.getBody();

    }

/*    private Object findRoute(RequestContext requestContext, String contextPath, String user, String location) {

        // Split by versions@hosts
        Set<MatchingLoc> matchingLocSet = Stream.of(location.split(";")).
                map(String::trim).
                map(MatchingLoc::new).
                collect(Collectors.toSet());

        // Call UserService to know which route to take for the given user
        Route userServiceRoute = routeLocator.getMatchingRoute("/user-service");
        String userServiceLocation = userServiceRoute.getLocation();
        URI uri = UriComponentsBuilder.fromHttpUrl(userServiceLocation).
                path("/api/v1/routes").
                path(contextPath + "/" + user).
                build().
                encode().
                toUri();

        ResponseEntity<String> versionToUse = restTemplate.getForEntity(uri, String.class);

        try {
            String newRoute = matchingLocSet.stream().
                    filter(p -> p.getVersion().equals(versionToUse.getBody())).
                    map(MatchingLoc::getLocation).
                    findAny().
                    orElseThrow(() -> new Exception("Route not found"));

            requestContext.setRouteHost(new URL(newRoute));
            return null;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            requestContext.unset();
            requestContext.getResponse().setContentType(MediaType.TEXT_HTML_VALUE);
            requestContext.setResponseStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            requestContext.setSendZuulResponse(false);
            return null;
        }
    }*/

    @Data
    private class MatchingLoc {
        private final String version;
        private final String location;

        private MatchingLoc(String locationByVersion) {
            String[] locationByVersionSplit = locationByVersion.split("@");
            this.version = locationByVersionSplit[0];
            this.location = locationByVersionSplit[1];
        }
    }

}