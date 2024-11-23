package com.tbank.edu.benchmarks;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class Consumer {

    public static final AtomicLong COUNT_OF_ALL_RECEIVED_MESSAGES = new AtomicLong(0);
    public static final AtomicLong COUNT_OF_SECONDS = new AtomicLong(0);

    private final Channel channel;

    private final AtomicLong countOfMessage = new AtomicLong(0);

    public Consumer(Channel channel) {
        this.channel = channel;
    }

    public void receiveMessage(String queue) {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> countOfMessage.getAndIncrement();
        try {
            channel.basicConsume(queue, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public long getCountOfMessage() {
        return countOfMessage.get();
    }
}
