package com.springboot.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.order-created}")
    private String topicName;

    @Value("${app.kafka.topic.partitions}")
    private int partitionCount;

    @Value("${app.kafka.topic.replicas}")
    private int replicaCount;

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(topicName)
                .partitions(partitionCount)
                .replicas(replicaCount)
                .build();
    }
}
