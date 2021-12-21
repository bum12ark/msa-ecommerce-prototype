package com.ecommerce.orderservice.domain.order.entity.listener;

import com.ecommerce.orderservice.domain.order.client.catalog.CatalogClient;
import com.ecommerce.orderservice.domain.order.client.catalog.ResponseCatalog;
import com.ecommerce.orderservice.domain.order.client.user.ResponseUser;
import com.ecommerce.orderservice.domain.order.client.user.UserClient;
import com.ecommerce.orderservice.domain.order.dto.OrderDto;
import com.ecommerce.orderservice.domain.order.entity.Order;
import com.ecommerce.orderservice.domain.order.entity.OrderLine;
import com.ecommerce.orderservice.domain.order.messagequeue.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

@Slf4j
public class OrderListener {

    @Autowired
    private ApplicationContext context;

    @PrePersist
    public void orderCheck(Order order) throws Exception {
        log.debug("OrderListener.orderCheck");
        UserClient userClient = context.getBean(UserClient.class);
        ResponseUser user = userClient.getUser(order.getUserId());

        log.debug("user = {}", user);

        CatalogClient catalogClient = context.getBean(CatalogClient.class);
        for (OrderLine orderLine : order.getOrderLines()) {
            ResponseCatalog catalogById = catalogClient.getCatalogById(orderLine.getCatalogId());
            log.debug("catalogById = {}", catalogById);

            if (orderLine.isGreaterStock(catalogById.getStockQuantity())) {
                // TODO: 2021-12-21 Kafka Producer / Consumer 형식으로 변경 요망
                throw new Exception("No Available stock!!!");
            }
        }
    }

    @PostPersist
    private void publishOrderPlace(Order order) {
        KafkaProducer kafkaProducer = context.getBean(KafkaProducer.class);

        OrderDto orderDto = order.toOrderDto();
        kafkaProducer.send("orderPlaced", orderDto);
    }
}
