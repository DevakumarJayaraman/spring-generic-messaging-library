
package com.example.messaging.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MessageProducerFactory {

    @Autowired
    private ApplicationContext context;

    public MessageProducer getProducer(String provider) {
        return (MessageProducer) context.getBean(provider);
    }
}
