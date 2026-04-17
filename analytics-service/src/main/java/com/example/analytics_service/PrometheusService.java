package com.example.analytics_service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PrometheusService {
    private final RestTemplate restTemplate = new RestTemplate();

    private final String PROM_URL = "http://localhost:9090"; // если port-forward

    private static final List<String> SERVICES = List.of(
            "user-service",
            "payment-service",
            "order-service",
            "inventory-service"
    );

    public double executeQuery(String query) {

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = PROM_URL + "/api/v1/query?query=" + encodedQuery;

            Map response = restTemplate.getForObject(url, Map.class);

            if (response == null) return 0.0;
            if (!"success".equals(response.get("status"))) return 0.0;

            Map data = (Map) response.get("data");
            List result = (List) data.get("result");

            if (result == null || result.isEmpty()) return 0.0;

            // берем уже АГРЕГИРОВАННОЕ значение из PromQL
            Map first = (Map) result.get(0);
            List value = (List) first.get("value");

            if (value == null || value.size() < 2) return 0.0;

            return Double.parseDouble(value.get(1).toString());

        } catch (Exception e) {
            System.out.println("Prometheus query failed: " + query);
            return 0.0;
        }
    }

    public double getRps(String service) {
        return executeQuery(
                String.format(
                        "sum(rate(http_server_requests_seconds_count{application=\"%s\"}[1m]))",
                        service
                )
        );
    }

    public double getErrorRate(String service) {
        String query = String.format(
                "sum(rate(http_server_requests_seconds_count{application=\"%s\", status=~\"5..\"}[1m])) / " +
                        "sum(rate(http_server_requests_seconds_count{application=\"%s\"}[1m]))",
                service, service
        );
        return executeQuery(query);
    }


    public double getLatencyP95(String service) {
        String query = String.format(
                "histogram_quantile(0.95, " +
                        "sum(rate(http_server_requests_seconds_bucket{application=\"%s\"}[1m])) by (le))",
                service
        );
        return executeQuery(query);
    }


    public double getLatencyMax(String service) {
        return executeQuery(
                String.format(
                        "max(http_server_requests_seconds_max{application=\"%s\"})",
                        service
                )
        );
    }

    public double getCpuUsage(String service) {
        return executeQuery(
                String.format(
                        "sum(rate(container_cpu_usage_seconds_total{pod=~\"%s.*\"}[1m]))",
                        service
                )
        );
    }

    public double getPodCount() {
        String query = "count(kube_pod_status_phase{phase=\"Running\"})";
        return executeQuery(query);
    }

    public List<ServiceMetrics> getAllServicesMetrics() {

        List<ServiceMetrics> result = new ArrayList<>();

        for (String service : SERVICES) {

            ServiceMetrics m = new ServiceMetrics();
            m.service = service;

            m.rps = getRps(service);
            m.errorRate = getErrorRate(service);
            m.latency = getLatencyP95(service);

            m.cpu = getCpuUsage(service);
            m.latencyMax = getLatencyMax(service);
            m.podCount = getPodCount();

            result.add(m);
        }

        return result;
    }


}
