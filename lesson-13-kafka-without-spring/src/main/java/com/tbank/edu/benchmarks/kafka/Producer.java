package com.tbank.edu.benchmarks.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Producer {

    private final KafkaProducer<String, String> kafkaProducer;

    public Producer(Properties properties) {
        this.kafkaProducer = new KafkaProducer<>(properties);
    }

    public void sendMessage(String topic, String message) throws ExecutionException, InterruptedException {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, message);
        var metadata = kafkaProducer.send(record);
        metadata.get();
    }
}
