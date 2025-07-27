
package com.example.messaging.producer;

import com.solacesystems.jcsmp.*;
import org.springframework.stereotype.Component;

@Component("solace")
public class SolaceMessageProducer implements MessageProducer {

    private JCSMPSession session;
    private XMLMessageProducer producer;

    public SolaceMessageProducer() {
        try {
            JCSMPProperties properties = new JCSMPProperties();
            properties.setProperty(JCSMPProperties.HOST, "tcp://localhost:55555");
            properties.setProperty(JCSMPProperties.USERNAME, "admin");
            properties.setProperty(JCSMPProperties.PASSWORD, "admin");
            properties.setProperty(JCSMPProperties.VPN_NAME, "default");
            
            session = JCSMPFactory.onlyInstance().createSession(properties);
            session.connect();
            producer = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
                @Override
                public void responseReceived(String messageID) {
                    // Handle response if needed
                }

                @Override
                public void handleError(String messageID, JCSMPException cause, long timestamp) {
                    System.err.println("Producer error: " + cause.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to initialize Solace producer: " + e.getMessage());
        }
    }

    @Override
    public void send(String topic, String message) {
        try {
            if (producer != null) {
                Topic solaceTopic = JCSMPFactory.onlyInstance().createTopic(topic);
                TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
                msg.setText(message);
                producer.send(msg, solaceTopic);
                System.out.println("Sent to Solace: " + message);
            } else {
                System.err.println("Solace producer not initialized");
            }
        } catch (Exception e) {
            System.err.println("Failed to send message to Solace: " + e.getMessage());
        }
    }
}
