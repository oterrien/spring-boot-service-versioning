package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class UserRestController {

    @Autowired
    private UserRoutingService userRoutingService;

    @RequestMapping(value = "/routes/{contextPath}/{user}", method = RequestMethod.GET)
    @ResponseBody
    public String find(@PathVariable("contextPath") String contextPath, @PathVariable(required = false, name = "user") String user) {
        log.info("search version for contextPath = " + contextPath + ", user " + user);
        return userRoutingService.getRoute(contextPath, user);
    }

    @RequestMapping(value = "/routes/{contextPath}/{user}", method = RequestMethod.PUT)
    @ResponseBody
    public void save(@PathVariable("contextPath") String contextPath, @PathVariable(name = "user") String user, @RequestBody String version) {
        log.info("put version = " + version + " for contextPath = " + contextPath + ", user " + user);
        userRoutingService.save(contextPath, user, version);
    }


    @RequestMapping(value = "/routes/{contextPath}/{user}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("contextPath") String contextPath, @PathVariable(name = "user") String user) {
        log.info("remove route for contextPath = " + contextPath + ", user " + user);
        userRoutingService.delete(contextPath, user);
    }

}
