
package com.example.messaging.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageListener {
    String provider(); // kafka, solace
    String topic() default ""; // For Kafka and Solace topics
    String queue() default ""; // For Solace queues
}
