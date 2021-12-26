package com.ecommerce.catalogservice.global.messegequeue;

import com.ecommerce.catalogservice.domain.catalog.entity.Catalog;
import com.ecommerce.catalogservice.domain.catalog.exception.NotExistCatalogException;
import com.ecommerce.catalogservice.domain.catalog.repository.CatalogRepository;
import com.ecommerce.catalogservice.domain.order.dto.OrderDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j @RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final CatalogRepository catalogRepository;

    @Transactional
    @KafkaListener(topics = "orderPlaced")
    public void updateQty(String kafkaMessage) throws Exception {
        log.info("KafkaConsumer.updateQty");
        log.info("Kafka message = {}", kafkaMessage);

        OrderDto orderDto = objectMapper.readValue(kafkaMessage, OrderDto.class);

        orderDto.getOrderLineDtoList()
                .forEach(orderLineDto -> {
                    Catalog catalog = catalogRepository.findById(orderLineDto.getCatalogId())
                            .orElseThrow(NotExistCatalogException::new);

                    catalog.updateQty(orderLineDto.getCount());
                });
    }
}
