package com.ecommerce.orderservice.domain.order.messagequeue;

import com.ecommerce.orderservice.domain.order.dto.OrderDto;
import com.ecommerce.orderservice.domain.order.entity.Order;
import com.ecommerce.orderservice.domain.order.exception.NotExistOrder;
import com.ecommerce.orderservice.domain.order.respository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j @RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @Transactional
    @KafkaListener(topics = "productChanged")
    public void productChanged(String kafkaMessage) throws Exception {
        log.info("KafkaConsumer.productChanged");
        log.info("Kafka message = {}", kafkaMessage);

        OrderDto orderDto = objectMapper.readValue(kafkaMessage, OrderDto.class);

        Order order = orderRepository.findById(orderDto.getId())
                .orElseThrow(NotExistOrder::new);

        order.updateStatus(orderDto.getOrderStatus());
    }
}
