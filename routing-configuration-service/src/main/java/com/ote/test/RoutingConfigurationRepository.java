package com.ote.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingConfigurationRepository extends JpaRepository<RoutingConfiguration, Integer> {

    RoutingConfiguration findByKey(@Param("key") RoutingConfiguration.Key key);
}
