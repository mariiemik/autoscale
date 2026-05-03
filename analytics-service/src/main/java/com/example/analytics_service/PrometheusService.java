package com.example.analytics_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PrometheusService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, List<ServiceMetrics>> history = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(PrometheusService.class);

    //    private final String PROM_URL = "http://localhost:9090"; // если port-forward
    private final String PROM_URL = "http://monitoring-kube-prometheus-prometheus:9090";

    //http://prometheus-kube-prometheus-prometheus:9090
    private static final List<String> SERVICES = List.of(
            "user-service",
            "payment-service",
            "order-service",
            "inventory-service"
    );

//    public double executeQuery(String query) {
//
//        try {
//            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
//            String url = PROM_URL + "/api/v1/query?query=" + encodedQuery;
//
//            Map response = restTemplate.getForObject(url, Map.class);
//
//            if (response == null) return 0.0;
//            if (!"success".equals(response.get("status"))) return 0.0;
//
//            Map data = (Map) response.get("data");
//            List result = (List) data.get("result");
//
//            if (result == null || result.isEmpty()) return 0.0;
//
//            // берем уже АГРЕГИРОВАННОЕ значение из PromQL
//            Map first = (Map) result.get(0);
//            List value = (List) first.get("value");
//
//            if (value == null || value.size() < 2) return 0.0;
//
//            return Double.parseDouble(value.get(1).toString());
//
//        } catch (Exception e) {
//            System.out.println("Prometheus query failed: " + query);
//            e.printStackTrace();
//
//            return 0.0;
//        }
//    }


    public double executeQuery(String query) {
        try {
            String encodedQuery = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
            java.net.URI uri = java.net.URI.create(PROM_URL + "/api/v1/query?query=" + encodedQuery);

//            System.out.println("PROMQL: " + query);
//            System.out.println("URI: " + uri);

            Map response = restTemplate.exchange(uri, org.springframework.http.HttpMethod.GET, null, Map.class).getBody();

            if (response == null || !"success".equals(response.get("status"))) {
                log.error("Prometheus error: {}", response);
                return 0.0;
            }

            Map data = (Map) response.get("data");
            List result = (List) data.get("result");

            if (result == null || result.isEmpty()) {
                return 0.0;
            }

            double sum = 0.0;

            for (Object obj : result) {
                Map item = (Map) obj;
                List value = (List) item.get("value");
                if (value != null && value.size() >= 2) {
                    sum += Double.parseDouble(value.get(1).toString());
                }
            }

            return sum;
        } catch (Exception e) {
            log.error("FAILED QUERY: {}", query, e);

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

    public double getPodCount(String service) {
        return executeQuery(
                String.format(
                        "count(kube_pod_status_ready{condition=\"true\", pod=~\"%s.*\"})",
                        service
                )
        );
    }

    public List<ServiceMetrics> getAllServicesMetrics() {

        List<ServiceMetrics> result = new ArrayList<>();

        for (String service : SERVICES) {

            ServiceMetrics m = new ServiceMetrics();
            m.service = service;

            m.rps = getRps(service);
//            m.errorRate = getErrorRate(service);
            m.latency = getLatencyP95(service);

            m.cpu = getCpuUsage(service);
            m.latencyMax = getLatencyMax(service);
            m.podCount = getPodCount(service);

            result.add(m);
        }

        return result;
    }

    @Scheduled(fixedRate = 10000)
    public void collectMetrics() {

        for (String service : SERVICES) {
            try {
                ServiceMetrics m = new ServiceMetrics();

                m.rps = getRps(service);
                m.latency = getLatencyP95(service);
                m.cpu = getCpuUsage(service);
                m.latencyMax = getLatencyMax(service);
                m.podCount = getPodCount(service);

                history.computeIfAbsent(service,
                                k -> Collections.synchronizedList(new ArrayList<>()))
                        .add(m);

            } catch (Exception e) {
                log.error("Failed metric collection for service={}", service, e);
            }
        }
    }


    public void historyClear() {
        history.clear();
    }

    public Map<String, List<ServiceMetrics>> getHistory() {
        return history;
    }
}
