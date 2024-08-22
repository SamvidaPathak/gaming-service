package com.project.gamingservice.config;

import com.project.gamingservice.contants.Constants;
import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.UUID;

@Data
@Configuration
public class KafkaConfigProperties {

    @Bean
    public Producer<String, Integer> createKafkaProducer()
    {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.KAFKA_BROKER);
        kafkaProps.put(ProducerConfig.ACKS_CONFIG, "-1");
        kafkaProps.put(ProducerConfig.RETRIES_CONFIG, "3");
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer" );
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer" );

        return new KafkaProducer<>(kafkaProps);
    }

    @Bean
    public KafkaConsumer createKafkaConsumer()
    {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG , Constants.KAFKA_BROKER);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG , "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG , "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG , "g" + UUID.randomUUID());
        return new KafkaConsumer<>(props);
    }

}
