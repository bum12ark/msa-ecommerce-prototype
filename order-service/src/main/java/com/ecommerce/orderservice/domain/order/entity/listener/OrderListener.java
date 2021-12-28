package com.ecommerce.orderservice.domain.order.entity.listener;

import com.ecommerce.orderservice.domain.order.client.catalog.CatalogClient;
import com.ecommerce.orderservice.domain.order.client.catalog.ResponseCatalog;
import com.ecommerce.orderservice.domain.order.client.user.ResponseUser;
import com.ecommerce.orderservice.domain.order.client.user.UserClient;
import com.ecommerce.orderservice.domain.order.dto.OrderDto;
import com.ecommerce.orderservice.domain.order.entity.Order;
import com.ecommerce.orderservice.domain.order.entity.OrderLine;
import com.ecommerce.orderservice.global.messagequeue.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;

@Slf4j
public class OrderListener {
    @Autowired @Lazy
    private UserClient userClient;
    @Autowired @Lazy
    private CatalogClient catalogClient;
    @Autowired @Lazy
    private KafkaProducer kafkaProducer;

    @PrePersist
    public void orderCheck(Order order) {
        log.debug("OrderListener.orderCheck");
        // 유저 정보 체크
        ResponseUser user = userClient.getUser(order.getUserId());

        // 카탈로그 정보 체크
        for (OrderLine orderLine : order.getOrderLines()) {
            ResponseCatalog catalogById = catalogClient.getCatalogById(orderLine.getCatalogId());
        }
    }

    @PostPersist
    private void publishOrderPlace(Order order) {
        OrderDto orderDto = order.toOrderDto();
        kafkaProducer.send("orderPlaced", orderDto);
    }

    @PostUpdate
    private void publishOrderCancelled(Order order) {
        if (!order.isCancel()) return;
        kafkaProducer.send("orderCancelled", order.toOrderDto());
    }
}
