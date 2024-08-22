package com.project.gamingservice.service;

import com.project.gamingservice.contants.Constants;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.FailOnInvalidTimestamp;
import org.springframework.stereotype.Service;
import java.util.Properties;

@Service
public class KStreamService extends Thread {

    private KafkaStreams streams;

    @Override
    public void run() {

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Integer> scores = builder.stream(Constants.INPUT_TOPIC, Consumed.with(Serdes.String(), Serdes.Integer()));
        KTable<String, Integer> highScores = builder.table(Constants.OUTPUT_TOPIC, Consumed.with(Serdes.String(), Serdes.Integer(),new FailOnInvalidTimestamp(), Topology.AutoOffsetReset.EARLIEST));

        scores.leftJoin( highScores , (v1,v2) -> {
                    if(v2 == null){
                        return v1;
                    }
                    if( v1 > v2 ) {
                        return v1;
                    }
                    return null;
                })
                .filter( (k,v) -> v != null ) //filter null values
                .to(Constants.OUTPUT_TOPIC , Produced.with(Serdes.String(),Serdes.Integer()));

        Topology topology = builder.build();
        Properties props = new Properties();
        props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG,"gaming-service-app");
        props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.KAFKA_BROKER);
        props.setProperty(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,"10000");
        streams = new KafkaStreams( topology , props );
        streams.cleanUp(); //TODO remove this line in a production environment

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                streams.close();
            }
        });
        streams.start();
    }

    public void stopStreaming(){
        streams.close();
    }
}

