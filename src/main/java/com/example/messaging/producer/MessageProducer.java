
package com.example.messaging.producer;

public interface MessageProducer {
    void send(String topic, String message);
}
