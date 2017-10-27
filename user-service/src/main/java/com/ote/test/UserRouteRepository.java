package com.ote.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRouteRepository extends JpaRepository<UserRouteEntity, Integer> {

    UserRouteEntity findByKey(@Param("key") UserRouteEntity.Key key);

    @Query(value = "select rc from UserRouteEntity rc where rc.key.contextPath=:contextPath")
    List<UserRouteEntity> findByContextPath(@Param("contextPath") String contextPath);

    @Query(value = "select rc from UserRouteEntity rc where rc.key.user=:user")
    List<UserRouteEntity> findByUser(@Param("user") String user);
}
