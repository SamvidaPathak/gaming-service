package com.project.gamingservice.config;

import com.project.gamingservice.contants.Constants;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TopicConfig {
    @Bean
    public KafkaAdmin admin()
    {
        Map<String, Object> configs = new HashMap<>();
        configs.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.KAFKA_BROKER);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1()
    {
        return TopicBuilder.name(Constants.INPUT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topic2()
    {
        return TopicBuilder.name(Constants.OUTPUT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

}
