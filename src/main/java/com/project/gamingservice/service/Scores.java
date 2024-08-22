package com.project.gamingservice.service;

import com.project.gamingservice.config.KafkaConfigProperties;
import com.project.gamingservice.model.Score;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.project.gamingservice.contants.Constants.INPUT_TOPIC;

@Service
public class Scores  implements Iterable<Score> {

    KafkaConfigProperties kafkaConfigProperties=new KafkaConfigProperties();

    @Override
    public Iterator<Score> iterator() {
        return new ScoresIterator();
    }

    private class ScoresIterator implements Iterator<Score> {

        final private ArrayBlockingQueue<Score> blockingQueue = new ArrayBlockingQueue<>(100);
        private ConsumerRecords<String,Integer> lastFetchedRecords;
        private ExecutorService executorService = Executors.newCachedThreadPool();

        public ScoresIterator() {

            new Thread( () -> {

                KafkaConsumer kafkaConsumer=kafkaConfigProperties.createKafkaConsumer();

                kafkaConsumer.subscribe(Collections.singletonList(INPUT_TOPIC));
                Future<?> future = null;

                while (true) {

                    ConsumerRecords<String, Integer> records = kafkaConsumer.poll(2300);

                    if( !records.isEmpty() ) {
                        lastFetchedRecords = records;
                        kafkaConsumer.pause( Collections.singleton( new TopicPartition(INPUT_TOPIC,0)) );

                        future = executorService.submit( () -> {
                            for (ConsumerRecord<String, Integer> record : lastFetchedRecords) {
                                try {
                                    blockingQueue.put( new Score( record.key() , record.value() , record.timestamp() ) );
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            lastFetchedRecords = null;
                        });
                    }

                    if( future != null && future.isDone() ){
                        kafkaConsumer.resume( Collections.singleton( new TopicPartition(INPUT_TOPIC,0)) );
                    }
                }
            }).start();
        }

        @Override
        public boolean hasNext() {
            // hasNext is always returns true because we dealing with streams of events
            // and by definition streams is infinite of events
            return true;
        }

        @Override
        public Score next() {
            try {
                return blockingQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }

    }
}

