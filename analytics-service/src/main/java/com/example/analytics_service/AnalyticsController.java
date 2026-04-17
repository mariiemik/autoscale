package com.example.analytics_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnalyticsController {
    private final PrometheusService service;

    AnalyticsController(PrometheusService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<ServiceMetrics> all() {
        return service.getAllServicesMetrics();
    }
}
