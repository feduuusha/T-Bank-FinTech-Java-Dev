package com.tbank.edu.benchmarks;

import com.tbank.edu.benchmarks.kafka.ConsumerTask;
import com.tbank.edu.benchmarks.kafka.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Slf4j
public class MultipleConsumerKafkaTest extends AbstractBenchmark {
    private static Producer producer;
    private static List<ConsumerTask> consumerTasks;
    private static ExecutorService executorService;


    @Setup(Level.Iteration)
    public static void init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        producer = new Producer(props);
        executorService = Executors.newFixedThreadPool(3);
        consumerTasks = new ArrayList<>(3);
        for (int i = 0; i < 3; ++i) {
            ConsumerTask consumerTask = new ConsumerTask(props, "my-topic");
            executorService.submit(consumerTask);
            consumerTasks.add(consumerTask);
        }
    }

    @Benchmark
    public void multipleConsumerConfig() throws ExecutionException, InterruptedException {
        producer.sendMessage("my-topic", "message");
    }

    @TearDown(Level.Iteration)
    public void tearDown() throws InterruptedException {
        consumerTasks.forEach(ConsumerTask::stop);
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        ConsumerTask.COUNT_OF_SECONDS.getAndAdd(10);
        log.warn("Средняя пропускная способность: " + (ConsumerTask.COUNT_OF_ALL_RECEIVED_MESSAGES.doubleValue() / ConsumerTask.COUNT_OF_SECONDS.get()) + " сообщений в секунду");
    }
}
