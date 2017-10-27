package com.ote.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "T_ROUTE_CONFIGURATION", uniqueConstraints = {@UniqueConstraint(columnNames = {"PATH", "VERSION"})})
@Data
@NoArgsConstructor
public class RoutingConfigurationEntity {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;

    @Embedded
    private Key key;

    @Column(name = "LOCATION")
    private String location;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key {

        @Column(name = "PATH")
        private String contextPath;

        @Column(name = "VERSION")
        private String version;
    }
}


