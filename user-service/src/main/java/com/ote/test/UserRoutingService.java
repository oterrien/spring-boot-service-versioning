package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserRoutingService {

    @Autowired
    private UserRouteRepository userRouteRepository;

    public String find(String contextPath, String user) {

        UserRoute.Key key = new UserRoute.Key(contextPath, user);
        return findByKey(key).map(UserRoute::getVersion).orElse(null);
    }

    private Optional<UserRoute> findByKey(UserRoute.Key key) {
        return Optional.ofNullable(userRouteRepository.findByKey(key));
    }

    public void save(String contextPath, String user, String version) {
        UserRoute.Key key = new UserRoute.Key(contextPath, user);
        UserRoute userRoute = new UserRoute();
        userRoute.setId(findByKey(key).map(UserRoute::getId).orElse(null));
        userRoute.setKey(key);
        userRoute.setVersion(version);
        userRouteRepository.save(userRoute);
    }

    public void delete(String contextPath, String user) {
        UserRoute.Key key = new UserRoute.Key(contextPath, user);
        findByKey(key).ifPresent(p -> userRouteRepository.delete(p.getId()));
    }
}
