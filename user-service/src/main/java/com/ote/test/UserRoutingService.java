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

    public UserRoutingService() {
        routeMapping.put(new Key("test-service", "user1"), "1.2.3");
    }

    public String getRoute(String contextPath, String user) {

        Key key = new Key(contextPath, user);
        return Optional.ofNullable(routeMapping.get(key)).orElse("1.1.19");
    }
}
