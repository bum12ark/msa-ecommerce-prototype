package com.ecommerce.orderservice.domain.order.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j @RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(String topic, Object target) {
        try {
            String jsonString = objectMapper.writeValueAsString(target);

            kafkaTemplate.send(topic, jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("Kafka Producer sent data = {}", target);
    }
}
