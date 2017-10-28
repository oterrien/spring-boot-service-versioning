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
public class RoutingConfigurationProxyService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClientRouteLocator routeLocator;

    @HystrixCommand(fallbackMethod = "routingConfigurationServiceNotAvailable")
    public String getLocation(String contextPath, String version, Supplier<String> defaultLocation) throws Exception {
        Route routingConfigurationServiceRoute = routeLocator.getMatchingRoute("/routes");
        String routingConfigurationServiceLocation = routingConfigurationServiceRoute.getLocation();
        URI routingConfigurationServiceUri = UriComponentsBuilder.fromHttpUrl(routingConfigurationServiceLocation).
                path("/api/v1/routes/location").
                queryParam("contextPath", contextPath).
                queryParam("version", version).
                build().encode().toUri();
        ResponseEntity<String> location = restTemplate.getForEntity(routingConfigurationServiceUri, String.class);
        return location.getBody();
    }

    public String routingConfigurationServiceNotAvailable(String contextPath, String version, Supplier<String> defaultLocation) {
        return defaultLocation.get();
    }
}
