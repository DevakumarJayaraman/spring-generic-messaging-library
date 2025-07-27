# Spring Generic Messaging Library

A unified Spring Boot library for messaging with multiple providers (Kafka and Solace) through a common interface.

## Features

- **Unified Messaging Interface**: Single API for multiple messaging providers
- **Pluggable Providers**: Support for Kafka and Solace messaging
- **Annotation-based Listeners**: Simple `@MessageListener` annotation for message consumption
- **REST API**: HTTP endpoints for sending messages
- **Spring Boot Integration**: Auto-configuration and dependency injection
- **Graceful Degradation**: Works even when brokers are unavailable

## Quick Start

### Dependencies

The library includes:
- Spring Boot Starter Web
- Spring Kafka
- Solace JCSMP

### Configuration

Configure messaging providers in `application.properties`:

```properties
# Kafka configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=my-app-group

# Server configuration
server.port=8080
```

### Sending Messages

Use the REST API to send messages:

```bash
# Send to Kafka
curl -X POST "http://localhost:8080/produce/kafka/my-topic" \
     -H "Content-Type: application/json" \
     -d "Hello Kafka!"

# Send to Solace
curl -X POST "http://localhost:8080/produce/solace/my-topic" \
     -H "Content-Type: application/json" \
     -d "Hello Solace!"
```

Or use the MessageProducerFactory programmatically:

```java
@Autowired
private MessageProducerFactory producerFactory;

public void sendMessage(String provider, String topic, String message) {
    MessageProducer producer = producerFactory.getProducer(provider);
    producer.send(topic, message);
}
```

### Receiving Messages

Annotate methods with `@MessageListener`:

```java
@Component
public class MyMessageHandler {

    @MessageListener(provider = "kafka", topic = "my-topic")
    public void handleKafkaMessage(String message) {
        System.out.println("Received from Kafka: " + message);
    }

    @MessageListener(provider = "solace", topic = "my-topic") 
    public void handleSolaceMessage(String message) {
        System.out.println("Received from Solace: " + message);
    }
}
```

## Running the Application

```bash
./gradlew bootRun
```

The application will start on port 8080 and automatically configure available messaging providers.

## Building

```bash
./gradlew build
```

## Testing

```bash
./gradlew test
```

## Architecture

The library uses a factory pattern for message producers and a custom annotation processor for message listeners, providing a clean abstraction over different messaging technologies.

