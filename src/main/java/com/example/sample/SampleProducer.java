
package com.example.sample;

import com.example.messaging.producer.MessageProducer;
import com.example.messaging.producer.MessageProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produce")
public class SampleProducer {

    @Autowired
    private MessageProducerFactory producerFactory;

    @PostMapping("/{provider}/{topic}")
    public String sendMessage(@PathVariable String provider,
                              @PathVariable String topic,
                              @RequestBody String message) {
        MessageProducer producer = producerFactory.getProducer(provider);
        producer.send(topic, message);
        return "Message sent to " + provider + " on topic " + topic;
    }
}
