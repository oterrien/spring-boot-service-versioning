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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class RoutingUserZuulFilter extends ZuulFilter {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClientRouteLocator routeLocator;


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

        String user = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION)).
                map(authorization -> {
                    if (authorization.startsWith("Basic")) {
                        String credentialsBase64 = authorization.replace("Basic", "").trim();
                        String credentials = new String(Base64.decode(credentialsBase64));
                        return credentials.substring(0, credentials.indexOf(":"));
                    }
                    return null;
                }).
                orElse(null);

        String requestUri = URI.create(request.getRequestURI()).getPath();

        String contextPath = requestUri.substring(0, requestUri.indexOf("/", 1));
        requestUri = requestUri.replaceAll(contextPath, "");

        Route matchingRoute = routeLocator.getMatchingRoute(contextPath);

        String location = matchingRoute.getLocation();
        if (location.contains(";")) {

            Set<MatchingLoc> matchingLocSet = Stream.of(location.split(";")).
                    map(String::trim).
                    map(MatchingLoc::new).
                    collect(Collectors.toSet());

            Route userServiceRoute = routeLocator.getMatchingRoute("/user-service");
            String userServiceLocation = userServiceRoute.getLocation();
            URI uri = UriComponentsBuilder.fromHttpUrl(userServiceLocation).
                    path("/api/v1/routes").
                    path(contextPath).
                    queryParam("user", user).
                    build().encode().toUri();

            ResponseEntity<String> versionToUse = restTemplate.getForEntity(uri, String.class);

            try {
                String newRoute = matchingLocSet.stream().
                        filter(p -> p.getVersion().equals(versionToUse.getBody())).
                        map(MatchingLoc::getLocation).
                        findAny().
                        orElseThrow(() -> new Exception("Route not found"));

                //requestContext.set("requestURI", UriComponentsBuilder.fromHttpUrl(newRoute).build().toUriString());
                requestContext.setRouteHost(new URL(newRoute));

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                requestContext.unset();
                requestContext.getResponse().setContentType(MediaType.TEXT_HTML_VALUE);
                requestContext.setResponseStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                requestContext.setSendZuulResponse(false);
                return null;
            }

        } else {
            return null;
        }


        // Si la config zuul pour le contextPath c'est url, alors on appelle cet url
        // Si la config zuul pour le contextPath c'est urls, alors on appelle UserService pour savoir quelle url appeler


        return null;
    }

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