package com.tbank.edu.becnhmarks;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tbank.edu.benchmarks.Consumer;
import com.tbank.edu.benchmarks.Producer;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Slf4j
public class LoadBalancingConfigRabbitTest extends AbstractBenchmark {

    private static List<Producer> producers;
    private static Consumer consumer;
    private static Connection connection;
    private static final String QUEUE_NAME = "queue";

    @Setup(Level.Iteration)
    public static void init() throws IOException, TimeoutException{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("rab");
        connectionFactory.setPassword("1234");
        connectionFactory.setVirtualHost("vhost");
        connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        producers = new ArrayList<>(3);
        for (int i = 0; i < 3; ++i) {
            producers.add(new Producer(channel));
        }
        consumer = new Consumer(channel);
        consumer.receiveMessage(QUEUE_NAME);
    }

    @Benchmark
    public void loadBalancingConfig() {
        producers.forEach(producer -> producer.sendMessage(QUEUE_NAME, "Test-message"));
    }

    @TearDown(Level.Iteration)
    public static void tearDown() throws IOException {
        Consumer.COUNT_OF_SECONDS.getAndAdd(10);
        Consumer.COUNT_OF_ALL_RECEIVED_MESSAGES.getAndAdd(consumer.getCountOfMessage());
        log.warn("Средняя пропускная способность: " + (Consumer.COUNT_OF_ALL_RECEIVED_MESSAGES.doubleValue() / Consumer.COUNT_OF_SECONDS.get()) + " сообщений в секунду");
        connection.close();
    }
}
