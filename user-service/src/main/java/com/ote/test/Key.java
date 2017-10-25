package com.ote.test;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
@RequiredArgsConstructor
public class Key {

    @Column(name = "PATH")
    private final String contextPath;

    @Column(name = "USER")
    private final String user;
}