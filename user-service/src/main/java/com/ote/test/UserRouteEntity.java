package com.ote.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "T_USER_ROUTE", uniqueConstraints = {@UniqueConstraint(columnNames = {"PATH", "USER"})})
@Data
@NoArgsConstructor
public class UserRouteEntity {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;

    @Embedded
    private Key key;

    @Column(name = "VERSION")
    private String version;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key {

        @Column(name = "PATH")
        private String contextPath;

        @Column(name = "USER")
        private String user;
    }
}


