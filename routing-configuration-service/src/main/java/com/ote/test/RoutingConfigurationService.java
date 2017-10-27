package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RoutingConfigurationService {

    @Autowired
    private RoutingConfigurationRepository routingConfigurationRepository;

    public List<RoutingConfigurationEntity> findAll() {
        return routingConfigurationRepository.findAll();
    }

    public String find(String contextPath, String version) {

        List<RoutingConfigurationEntity> routingConfigurations =
                routingConfigurationRepository.findByContextPath(contextPath);

        VersionFinder.ExtendedVersion extendedVersion = new VersionFinder.ExtendedVersion(version);

        return routingConfigurations.
                stream().
                filter(rc -> extendedVersion.accept(new VersionFinder.Version(rc.getKey().getVersion()))).
                max((rc1, rc2) -> {
                    VersionFinder.Version v1 = new VersionFinder.Version(rc1.getKey().getVersion());
                    VersionFinder.Version v2 = new VersionFinder.Version(rc2.getKey().getVersion());
                    return v1.compareTo(v2);
                }).
                map(rc -> rc.getLocation()).
                orElse(null);
    }

    private Optional<RoutingConfigurationEntity> findByKey(RoutingConfigurationEntity.Key key) {
        return Optional.ofNullable(routingConfigurationRepository.findByKey(key));
    }

    public void save(String contextPath, String version, String location) {
        RoutingConfigurationEntity.Key key = new RoutingConfigurationEntity.Key(contextPath, version);
        RoutingConfigurationEntity entity = new RoutingConfigurationEntity();
        entity.setId(findByKey(key).map(RoutingConfigurationEntity::getId).orElse(null));
        entity.setKey(key);
        entity.setLocation(location);
        routingConfigurationRepository.save(entity);
    }

    public void delete(String contextPath, String version) {
        if (contextPath != null && version != null) {
            RoutingConfigurationEntity.Key key = new RoutingConfigurationEntity.Key(contextPath, version);
            findByKey(key).ifPresent(p -> routingConfigurationRepository.delete(p.getId()));
        } else if (contextPath != null) {
            routingConfigurationRepository.findByContextPath(contextPath).forEach(p -> routingConfigurationRepository.delete(p.getId()));
        } else if (version != null) {
            routingConfigurationRepository.findByVersion(version).forEach(p -> routingConfigurationRepository.delete(p.getId()));
        } else {
            routingConfigurationRepository.deleteAll();
        }
    }
}
