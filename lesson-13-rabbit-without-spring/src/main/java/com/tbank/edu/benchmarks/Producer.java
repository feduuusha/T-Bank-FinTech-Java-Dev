package com.tbank.edu.benchmarks;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Producer {

    private final Channel channel;

    public Producer(Channel channel) {
        this.channel = channel;
    }

    public void sendMessage(String queue, String message) {
        try {
            channel.basicPublish("", queue, null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
