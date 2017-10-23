package com.ote.test;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Key {
    private final String contextPath;
    private final String user;
}