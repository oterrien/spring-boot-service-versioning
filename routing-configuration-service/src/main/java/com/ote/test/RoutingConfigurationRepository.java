package com.ote.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutingConfigurationRepository extends JpaRepository<RoutingConfigurationEntity, Integer> {

    RoutingConfigurationEntity findByKey(@Param("key") RoutingConfigurationEntity.Key key);

    @Query(value = "select rc from RoutingConfigurationEntity rc where rc.key.contextPath=:contextPath")
    List<RoutingConfigurationEntity> findByContextPath(@Param("contextPath") String contextPath);

    @Query(value = "select rc from RoutingConfigurationEntity rc where rc.key.version=:version")
    List<RoutingConfigurationEntity> findByVersion(@Param("version") String version);
}
