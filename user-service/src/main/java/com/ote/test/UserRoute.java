package com.ote.test;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "T_USER_ROUTE")
@Data
@NoArgsConstructor
public class UserRoute {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;

    @Embedded
    private Key key;

    @Column(name = "VERSION")
    private String version;
}
