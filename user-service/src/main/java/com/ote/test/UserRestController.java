package com.ote.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserRestController {

    @Autowired
    private UserRoutingService userRoutingService;

    @RequestMapping(value = "/routes/{contextPath}", method = RequestMethod.GET)
    @ResponseBody
    public String getRoute(@PathVariable("contextPath") String contextPath, @RequestParam(required = false, name = "user") String user) {

        return userRoutingService.getRoute(contextPath, user);
    }


}
