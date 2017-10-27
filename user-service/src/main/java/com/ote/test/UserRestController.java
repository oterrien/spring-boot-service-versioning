package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/routes")
@Slf4j
public class UserRestController {

    private AtomicLong count = new AtomicLong(0);

    @Autowired
    private UserRoutingService userRoutingService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<UserRouteEntity> findAll() {
        return userRoutingService.findAll();
    }

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    @ResponseBody
    public String findVersion(@RequestParam("contextPath") String contextPath,
                       @RequestParam("user") String user) {
        long cur = count.getAndIncrement();
        log.info("#" + cur + " - search version for contextPath = " + contextPath + ", user " + user);
        String version = userRoutingService.find(contextPath, user);
        log.info("#" + cur + " - version " + version + " for contextPath = " + contextPath + ", user " + user);
        return version;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public void save(@RequestBody UserRoutePayload userRoutePayload) {
        long cur = count.getAndIncrement();
        log.info("#" + cur + " - put version = " + userRoutePayload.getVersion() + " for user = " + userRoutePayload.getUser() + " and contextPath = " + userRoutePayload.getContextPath());
        userRoutingService.save(userRoutePayload.getContextPath(), userRoutePayload.getUser(), userRoutePayload.getVersion());
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@RequestParam(name = "contextPath", required = false) String contextPath,
                       @RequestParam(name = "user", required = false) String user) {
        long cur = count.getAndIncrement();
        log.info("#" + cur + " - remove version for contextPath = " + contextPath + ", user " + user);
        userRoutingService.delete(contextPath, user);
    }

}
