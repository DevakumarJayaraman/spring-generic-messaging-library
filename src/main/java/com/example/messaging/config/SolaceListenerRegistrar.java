package com.example.messaging.config;

import com.example.messaging.annotation.MessageListener;
import com.solacesystems.jcsmp.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class SolaceListenerRegistrar {
    private JCSMPSession session;

    public SolaceListenerRegistrar() {
        try {
            JCSMPProperties properties = new JCSMPProperties();
            properties.setProperty(JCSMPProperties.HOST, "tcp://localhost:55555");
            properties.setProperty(JCSMPProperties.USERNAME, "admin");
            properties.setProperty(JCSMPProperties.PASSWORD, "admin");
            properties.setProperty(JCSMPProperties.VPN_NAME, "default");
            session = JCSMPFactory.onlyInstance().createSession(properties);
            session.connect();
        } catch (Exception e) {
            System.err.println("Failed to initialize Solace session: " + e.getMessage());
        }
    }

    public void registerListener(Object bean, Method method, MessageListener listener) throws JCSMPException {
        if (session == null) {
            System.err.println("Solace session not initialized, cannot register listener");
            return;
        }

        String topicName = listener.topic();
        String queueName = listener.queue();
        
        if (!topicName.isEmpty()) {
            // Register topic listener
            Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);
            XMLMessageConsumer consumer = session.getMessageConsumer(new XMLMessageListener() {
                @Override
                public void onReceive(BytesXMLMessage msg) {
                    try {
                        String payload = "";
                        if (msg instanceof TextMessage) {
                            payload = ((TextMessage) msg).getText();
                        } else if (msg.getUserData() != null) {
                            payload = new String(msg.getUserData());
                        }
                        method.setAccessible(true);
                        method.invoke(bean, payload);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onException(JCSMPException e) {
                    e.printStackTrace();
                }
            });
            consumer.start();
            System.out.printf("[SOLACE] Listener registered for topic %s%n", topicName);
        } else if (!queueName.isEmpty()) {
            // Register queue listener
            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
            ConsumerFlowProperties flowProps = new ConsumerFlowProperties();
            flowProps.setEndpoint(queue);
            flowProps.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_AUTO);
            EndpointProperties endpointProps = new EndpointProperties();
            FlowReceiver consumer = session.createFlow(new XMLMessageListener() {
                @Override
                public void onReceive(BytesXMLMessage msg) {
                    try {
                        String payload = "";
                        if (msg instanceof TextMessage) {
                            payload = ((TextMessage) msg).getText();
                        } else if (msg.getUserData() != null) {
                            payload = new String(msg.getUserData());
                        }
                        method.setAccessible(true);
                        method.invoke(bean, payload);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onException(JCSMPException e) {
                    e.printStackTrace();
                }
            }, flowProps, endpointProps);
            consumer.start();
            System.out.printf("[SOLACE] Listener registered for queue %s%n", queueName);
        } else {
            System.err.println("Neither topic nor queue specified for Solace listener");
        }
    }
}
