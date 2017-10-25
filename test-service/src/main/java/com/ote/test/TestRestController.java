package com.ote.test;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping(value = "/api/v1")
@Slf4j
public class TestRestController {

    private AtomicInteger count = new AtomicInteger(0);

    @Value("${value}")
    private String value;

    @Value("${version}")
    private String version;

    @CrossOrigin
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String getValue(@RequestHeader(name = "Authorization", required = false) String authorization) {

        String user = Optional.ofNullable(authorization).
                map(a -> {
                    if (a.startsWith("Basic")) {
                        String credentialsBase64 = a.replace("Basic", "").trim();
                        String credentials = new String(Base64.decode(credentialsBase64));
                        return credentials.substring(0, credentials.indexOf(":"));
                    }
                    return null;
                }).
                orElse("unknown");

        log.info("test #" + count.getAndIncrement() + " -> user : " + user);
        return value + "_" + version;
    }

}