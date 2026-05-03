package com.example.analytics_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AnalyticsController {
    private final PrometheusService service;
    private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);

    AnalyticsController(PrometheusService service) {
        this.service = service;
    }

//    @GetMapping("/all")
//    public List<ServiceMetrics> all() {
//        return service.getAllServicesMetrics();
//    }

    @GetMapping("/all")
    public Map<String, List<ServiceMetrics>> history() {
        log.info("called /all endpoint");
        return service.getHistory();
//        return history;
    }

    @GetMapping("/reset")
    public void resetHistory() {
        log.info("called /reset endpoint");
        service.historyClear();
//        history.clear();
    }
}
