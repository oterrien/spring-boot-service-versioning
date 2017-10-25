package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RoutingConfigurationService {

    @Autowired
    private RoutingConfigurationRepository routingConfigurationRepository;


    public String find(String contextPath, String version) {

        RoutingConfiguration.Key key = new RoutingConfiguration.Key(contextPath, version);
        return findByKey(key).map(RoutingConfiguration::getLocation).orElse(null);
    }

    private Optional<RoutingConfiguration> findByKey(RoutingConfiguration.Key key) {
        return Optional.ofNullable(routingConfigurationRepository.findByKey(key));
    }

    public void save(String contextPath, String version, String location) {
        RoutingConfiguration.Key key = new RoutingConfiguration.Key(contextPath, version);
        RoutingConfiguration userRoute = new RoutingConfiguration();
        userRoute.setId(findByKey(key).map(RoutingConfiguration::getId).orElse(null));
        userRoute.setKey(key);
        userRoute.setLocation(location);
        routingConfigurationRepository.save(userRoute);
    }


    public void delete(String contextPath, String user) {
        RoutingConfiguration.Key key = new RoutingConfiguration.Key(contextPath, user);
        findByKey(key).ifPresent(p -> routingConfigurationRepository.delete(p.getId()));
    }
}
