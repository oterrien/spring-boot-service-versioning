package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserRoutingService {

    @Autowired
    private UserRouteRepository userRouteRepository;

    public List<UserRouteEntity> findAll() {
        return userRouteRepository.findAll();
    }

    public String find(String contextPath, String user) {

        UserRouteEntity.Key key = new UserRouteEntity.Key(contextPath, user);
        return findByKey(key).map(UserRouteEntity::getVersion).orElse(null);
    }

    private Optional<UserRouteEntity> findByKey(UserRouteEntity.Key key) {
        return Optional.ofNullable(userRouteRepository.findByKey(key));
    }

    public void save(String contextPath, String user, String version) {
        UserRouteEntity.Key key = new UserRouteEntity.Key(contextPath, user);
        UserRouteEntity entity = new UserRouteEntity();
        entity.setId(findByKey(key).map(UserRouteEntity::getId).orElse(null));
        entity.setKey(key);
        entity.setVersion(version);
        userRouteRepository.save(entity);
    }

    public void delete(String contextPath, String user) {

        if (contextPath != null && user != null) {
            UserRouteEntity.Key key = new UserRouteEntity.Key(contextPath, user);
            findByKey(key).ifPresent(p -> userRouteRepository.delete(p.getId()));
        } else if (contextPath != null) {
            userRouteRepository.findByContextPath(contextPath).forEach(p -> userRouteRepository.delete(p.getId()));
        } else if (user != null) {
            userRouteRepository.findByUser(user).forEach(p -> userRouteRepository.delete(p.getId()));
        } else {
            userRouteRepository.deleteAll();
        }
    }
}
