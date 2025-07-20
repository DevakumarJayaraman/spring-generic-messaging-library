package com.example.messaging.config;

import com.example.messaging.annotation.MessageListener;
import com.solacesystems.jcsmp.JCSMPException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class MessageListenerRegistrar implements BeanPostProcessor {
    @Autowired
    private KafkaListenerRegistrar kafkaListenerRegistrar;
    @Autowired
    private SolaceListenerRegistrar solaceListenerRegistrar;

    private void registerListener(Object bean, Method method, com.example.messaging.annotation.MessageListener listener) throws JCSMPException {
        switch (listener.provider().toLowerCase()) {
            case "kafka":
                kafkaListenerRegistrar.registerListener(bean, method, listener);
                break;
            case "solace":
                solaceListenerRegistrar.registerListener(bean, method, listener);
                break;
            default:
                System.out.printf("[UNKNOWN PROVIDER] %s for topic %s%n", listener.provider(), listener.topic());
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(MessageListener.class)) {
                MessageListener listener = method.getAnnotation(MessageListener.class);
                try {
                    registerListener(bean, method, listener);
                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
}
