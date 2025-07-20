package com.example.messaging.config;

import com.example.messaging.annotation.MessageListener;
import com.solacesystems.jcsmp.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class SolaceListenerRegistrar {
    private JCSMPSession session;

    public SolaceListenerRegistrar() {
        JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, "tcp://localhost:55555"); // Update with your broker
        properties.setProperty(JCSMPProperties.USERNAME, "your-username");
        properties.setProperty(JCSMPProperties.PASSWORD, "your-password");
        properties.setProperty(JCSMPProperties.VPN_NAME, "default");
        try {
            session = JCSMPFactory.onlyInstance().createSession(properties);
            session.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerListener(Object bean, Method method, MessageListener listener) throws JCSMPException {
        Queue queue = JCSMPFactory.onlyInstance().createQueue("");
        ConsumerFlowProperties flowProps = new ConsumerFlowProperties();
        flowProps.setEndpoint(queue);
        flowProps.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_AUTO);
        EndpointProperties endpointProps = new EndpointProperties();
        FlowReceiver consumer = session.createFlow(new XMLMessageListener() {
            @Override
            public void onReceive(BytesXMLMessage msg) {
                try {
                    String payload = msg.getUserData() != null ? new String(msg.getUserData()) : "";
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
        System.out.printf("[SOLACE JCSMP] Listener registered for queue %s%n", listener.queue());
    }
}
