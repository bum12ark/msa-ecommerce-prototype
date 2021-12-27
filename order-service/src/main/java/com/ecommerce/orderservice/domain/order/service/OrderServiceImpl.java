package com.ecommerce.orderservice.domain.order.service;

import com.ecommerce.orderservice.domain.order.dto.DeliveryDto;
import com.ecommerce.orderservice.domain.order.dto.OrderDto;
import com.ecommerce.orderservice.domain.order.entity.Address;
import com.ecommerce.orderservice.domain.order.entity.Delivery;
import com.ecommerce.orderservice.domain.order.entity.Order;
import com.ecommerce.orderservice.domain.order.entity.OrderLine;
import com.ecommerce.orderservice.domain.order.exception.NotExistOrder;
import com.ecommerce.orderservice.domain.order.respository.OrderRepository;
import com.ecommerce.orderservice.global.messagequeue.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto createOrderDto) {
        DeliveryDto deliveryDto = createOrderDto.getDeliveryDto();
        Address address = new Address(deliveryDto.getCity(), deliveryDto.getStreet(), deliveryDto.getZipcode());
        Delivery delivery = Delivery.createDelivery(address);

        List<OrderLine> orderLines = createOrderDto.getOrderLineDtoList()
                .stream()
                .map(dto -> OrderLine.createOrderLine(dto.getOrderPrice(), dto.getCount(), dto.getCatalogId()))
                .collect(Collectors.toList());

        Order order = Order.createOrder(1L, delivery, orderLines);

        Order savedOrder = orderRepository.save(order);

        return savedOrder.toOrderDto();
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(NotExistOrder::new);
        order.pending();

        OrderDto orderDto = order.toOrderDto();
        kafkaProducer.send("orderCancelled", orderDto);
        return orderDto;
    }
}
