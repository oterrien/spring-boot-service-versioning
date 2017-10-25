package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class RoutingConfigurationRestController {

    @Autowired
    private RoutingConfigurationService routingConfigurationService;

    @RequestMapping(value = "/routes/{contextPath}/{version}/location", method = RequestMethod.GET)
    @ResponseBody
    public String find(@PathVariable("contextPath") String contextPath, @PathVariable(name = "version") String version) {
        log.info("search location for contextPath = " + contextPath + ", version " + version);
        return routingConfigurationService.find(contextPath, version);
    }

    @RequestMapping(value = "/routes/{contextPath}/{version}/location", method = RequestMethod.PUT)
    @ResponseBody
    public void save(@PathVariable("contextPath") String contextPath, @PathVariable(name = "version") String version, @RequestBody String location) {
        log.info("put location = " + location + " for contextPath = " + contextPath + ", version " + version);
        routingConfigurationService.save(contextPath, version, location);
    }


    @RequestMapping(value = "/routes/{contextPath}/{version}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("contextPath") String contextPath, @PathVariable(name = "version") String version) {
        log.info("remove location for contextPath = " + contextPath + ", version " + version);
        routingConfigurationService.delete(contextPath, version);
    }

}
