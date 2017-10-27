package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/routes")
@Slf4j
public class RoutingConfigurationRestController {

    private AtomicLong count = new AtomicLong(0);

    @Autowired
    private RoutingConfigurationService routingConfigurationService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<RoutingConfigurationEntity> findAll() {
        return routingConfigurationService.findAll();
    }

    @RequestMapping(value = "/location", method = RequestMethod.GET)
    @ResponseBody
    public String find(@RequestParam("contextPath") String contextPath,
                       @RequestParam("version") String version) {
        long cur = count.getAndIncrement();
        log.info("#" + cur + " - search location for contextPath = " + contextPath + ", version " + version);
        String location = routingConfigurationService.find(contextPath, version);
        log.info("#" + cur + " - location " + location + " for contextPath = " + contextPath + ", version " + version);
        return location;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public void save(@RequestBody RoutingConfigurationPayload userRoutePayload) {
        long cur = count.getAndIncrement();
        log.info("#" + cur + " - put location = " + userRoutePayload.getLocation() + " for contextPath = " + userRoutePayload.getContextPath() + " and version = " + userRoutePayload.getVersion());
        routingConfigurationService.save(userRoutePayload.getContextPath(), userRoutePayload.getVersion(), userRoutePayload.getLocation());
    }


    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public void delete(@RequestParam(name = "contextPath", required = false) String contextPath,
                       @RequestParam(name = "version", required = false) String version) {
        long cur = count.getAndIncrement();
        log.info("#" + cur + " - remove location for contextPath = " + contextPath + ", version " + version);
        routingConfigurationService.delete(contextPath, version);
    }

}
