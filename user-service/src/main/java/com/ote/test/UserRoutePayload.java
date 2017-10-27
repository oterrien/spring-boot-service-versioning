package com.ote.test;

import lombok.Data;

@Data
public class UserRoutePayload {

    private String contextPath;
    private String user;
    private String version;
}
