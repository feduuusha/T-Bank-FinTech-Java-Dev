package com.tbank.edu.benchmarks.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
public class ConsumerTask implements Runnable {

    public static final AtomicLong COUNT_OF_ALL_RECEIVED_MESSAGES = new AtomicLong(0);
    public static final AtomicLong COUNT_OF_SECONDS = new AtomicLong(0);

    private final KafkaConsumer<String, String> consumer;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final String topic;
    private final AtomicLong countOfMessage = new AtomicLong(0);


    public ConsumerTask(Properties properties, String topic) {
        this.consumer = new KafkaConsumer<>(properties);
        this.topic = topic;
    }

    public void stop() {
        running.set(false);
    }

    @Override
    public void run() {
            consumer.subscribe(Collections.singletonList(topic));

            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10));
                for (ConsumerRecord<String, String> record : records) {
                    processMessage(record);
                }
            }
            log.warn("Count of received messages: " + countOfMessage);
            COUNT_OF_ALL_RECEIVED_MESSAGES.getAndAdd(countOfMessage.get());
    }

    private void processMessage(ConsumerRecord<String, String> record) {
        countOfMessage.getAndIncrement();
    }
}