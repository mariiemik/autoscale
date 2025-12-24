package com.example.payment_service.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.kafka.KafkaClientMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaMetricsConfig {


    @Bean
    public KafkaClientMetrics kafkaClientMetrics(ConsumerFactory<String, Object> consumerFactory) {
        // Этот бин принудительно вытащит все внутренние счетчики Kafka в Actuator
        return new KafkaClientMetrics(consumerFactory.createConsumer());
    }
}
