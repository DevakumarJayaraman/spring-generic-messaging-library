
package com.example.sample;

import com.example.messaging.annotation.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class SampleConsumer {

    @MessageListener(provider = "kafka", topic = "test-topic")
    public void handleKafka(String message) {
        System.out.println("Received from Kafka: " + message);
    }

    @MessageListener(provider = "solace", topic = "test-topic")
    public void handleSolace(String message) {
        System.out.println("Received from Solace: " + message);
    }
}
