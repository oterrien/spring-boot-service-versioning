package com.ote.test;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.function.Supplier;

@Service
@Slf4j
public class UserServiceProxyService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClientRouteLocator routeLocator;

    @HystrixCommand(fallbackMethod = "userServiceNotAvailable")
    public String getVersion(String contextPath, String user, Supplier<String> defaultLocation) {
        Route userServiceRoute = routeLocator.getMatchingRoute("/users");
        String userServiceLocation = userServiceRoute.getLocation();
        URI userServiceUri = UriComponentsBuilder.fromHttpUrl(userServiceLocation).
                path("/api/v1/routes/version").
                queryParam("contextPath", contextPath).
                queryParam("user", user).
                build().encode().toUri();
        ResponseEntity<String> version = restTemplate.getForEntity(userServiceUri, String.class);
        return version.getBody();
    }

    public String userServiceNotAvailable(String contextPath, String user, Supplier<String> defaultLocation) {
        return defaultLocation.get();
    }
}
