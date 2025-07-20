
package com.example.messaging.producer;

import com.solace.messaging.publisher.DirectMessagePublisher;
import com.solace.messaging.resources.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("solace")
public class SolaceMessageProducer implements MessageProducer {

    @Autowired
    private DirectMessagePublisher solacePublisher;

    @Override
    public void send(String topic, String message) {
        solacePublisher.publish(message, Topic.of(topic));
        System.out.println("Sent to Solace: " + message);
    }
}
