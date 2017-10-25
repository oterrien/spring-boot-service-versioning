package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserRoutingService {

    private final Map<Key, String> routeMapping = new HashMap<>();

    public String getRoute(String contextPath, String user) {

        Key key = new Key(contextPath, user);
        synchronized (routeMapping) {
            return Optional.ofNullable(routeMapping.get(key)).orElse("1.1.19");
        }
    }

    public void save(String contextPath, String user, String version) {
        Key key = new Key(contextPath, user);
        synchronized (routeMapping) {
            routeMapping.put(key, version);
        }
    }

    public void delete(String contextPath, String user) {
        Key key = new Key(contextPath, user);
        synchronized (routeMapping) {
            routeMapping.remove(key);
        }
    }
}
