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
public class MultipleConsumerConfigRabbitTest extends AbstractBenchmark {

    private static Producer producer;
    private static List<Consumer> consumers;
    private static Connection connection;
    private static final String QUEUE_NAME = "queue";

    @Setup(Level.Iteration)
    public static void init() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("rab");
        connectionFactory.setPassword("1234");
        connectionFactory.setVirtualHost("vhost");
        connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        producer = new Producer(channel);
        consumers = new ArrayList<>(3);
        for (int i = 0; i < 3; ++i) {
            var consumer = new Consumer(channel);
            consumers.add(consumer);
            consumer.receiveMessage(QUEUE_NAME);
        }
    }

    @Benchmark
    public void multipleConsumerConfig() {
        producer.sendMessage(QUEUE_NAME, "Test-message");
    }

    @TearDown(Level.Iteration)
    public static void tearDown() throws IOException {
        Consumer.COUNT_OF_SECONDS.getAndAdd(10);
        consumers.forEach(consumer ->
                Consumer.COUNT_OF_ALL_RECEIVED_MESSAGES.getAndAdd(consumer.getCountOfMessage()));
        log.warn("Средняя пропускная способность: " + (Consumer.COUNT_OF_ALL_RECEIVED_MESSAGES.doubleValue() / Consumer.COUNT_OF_SECONDS.get()) + " сообщений в секунду");
        connection.close();
    }
}
