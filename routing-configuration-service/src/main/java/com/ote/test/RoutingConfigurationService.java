package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoutingConfigurationService {

    @Autowired
    private RoutingConfigurationRepository routingConfigurationRepository;

    public String find(String contextPath, String version) {

        List<String> allVersions =
                routingConfigurationRepository.findByContextPath(contextPath).
                        stream().
                        map(p -> p.getKey().getVersion()).
                        collect(Collectors.toList());

        return new VersionFinder(allVersions).find(version).orElse(null);

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
