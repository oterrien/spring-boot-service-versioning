package com.ote.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutingConfigurationRepository extends JpaRepository<RoutingConfiguration, Integer> {

    @Query(value = "select rc from RoutingConfiguration rc where rc.key.contextPath=:contextPath")
    List<RoutingConfiguration> findByContextPath(@Param("contextPath") String contextPath);

    RoutingConfiguration findByKey(@Param("key") RoutingConfiguration.Key key);
}
