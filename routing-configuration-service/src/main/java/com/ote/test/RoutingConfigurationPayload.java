package com.ote.test;

import lombok.Data;

@Data
public class RoutingConfigurationPayload {

    private String contextPath;
    private String version;
    private String location;
}
