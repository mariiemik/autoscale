//package com.example.payment_service.metric;
//
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.Tags;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.common.Metric;
//import org.apache.kafka.common.MetricName;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.Map;
//
//@Component
//public class KafkaMetrics {
//    private final MeterRegistry meterRegistry;
//    private final KafkaConsumer<String, Object> consumer;
//
//    public KafkaMetrics(MeterRegistry meterRegistry, KafkaConsumer<String, Object> consumer) {
//        this.meterRegistry = meterRegistry;
//        this.consumer = consumer;
//    }
//
//    @Scheduled(fixedRate = 5000) // каждые 5 секунд
//    public void updateLag() {
//        Map<MetricName, ? extends Metric> metrics = consumer.metrics();
//        Metric lagMetric = metrics.get(
//                new MetricName("records-lag-max", "consumer-fetch-manager-metrics", "", Collections.emptyMap())
//        );
//        if (lagMetric != null) {
//            double lag = (Double) lagMetric.metricValue();
//            meterRegistry.gauge("kafka_consumer_lag_max", Tags.of("payment-topic", "order-created"), lag);
//        }
//    }
//}
