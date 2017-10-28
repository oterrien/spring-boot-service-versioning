package com.ote.test;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

@Component
@Slf4j
public class RoutingUserZuulFilter extends ZuulFilter {

    @Autowired
    private RoutingConfigurationProxyService routingConfigurationProxyService;

    @Autowired
    private UserServiceProxyService userServiceProxyService;

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
            URL url = new URL(getServiceLocation(contextPath, user, matchingRoute));
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

    private String getServiceLocation(String contextPath, String user, String matchingRoute) throws Exception {
        if (user != null) {
            String version = userServiceProxyService.getVersion(contextPath, user, () -> getDefaultServiceLocation(matchingRoute));
            if (version != null) {
                String location = routingConfigurationProxyService.getLocation(contextPath, version, () -> getDefaultServiceLocation(matchingRoute));
                if (location != null) {
                    log.info("For contextPath " + contextPath + ", redirect User " + user + " to " + location);
                    return location;
                }
            }
        }
        return getDefaultServiceLocation(matchingRoute);
    }

    private String getDefaultServiceLocation(String matchingRoute) {
        return matchingRoute.split("\\|")[1];
    }


}