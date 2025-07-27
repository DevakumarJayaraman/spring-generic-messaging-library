package com.example.messaging.config;

import com.example.messaging.annotation.MessageListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class KafkaListenerRegistrar {
    @Autowired(required = false)
    private ConsumerFactory<String, String> kafkaConsumerFactory;

    public void registerListener(Object bean, Method method, MessageListener listener) {
        if (kafkaConsumerFactory != null) {
            ContainerProperties containerProps = new ContainerProperties(listener.topic());
            containerProps.setMessageListener(new org.springframework.kafka.listener.MessageListener<String, String>() {
                @Override
                public void onMessage(ConsumerRecord<String, String> record) {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean, record.value());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(kafkaConsumerFactory, containerProps);
            container.start();
            System.out.printf("[KAFKA] Listener registered for topic %s%n", listener.topic());
        } else {
            System.out.println("Kafka ConsumerFactory not available.");
        }
    }
}

