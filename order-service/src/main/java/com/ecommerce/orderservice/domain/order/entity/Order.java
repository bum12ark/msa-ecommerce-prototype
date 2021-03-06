package com.ecommerce.orderservice.domain.order.entity;

import com.ecommerce.orderservice.domain.order.dto.OrderDto;
import com.ecommerce.orderservice.domain.order.dto.OrderLineDto;
import com.ecommerce.orderservice.domain.order.entity.listener.OrderListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = {OrderListener.class})
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderLine> orderLines = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // == 연관관계 편의 메서드 == //
    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
        orderLine.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // == 생성 메소드 == //
    public static Order createOrder(Long userId, Delivery delivery, List<OrderLine> orderLines) {
        Order order = new Order();
        order.userId = userId;
        order.delivery = delivery;
        for (OrderLine orderLine : orderLines) {
            order.addOrderLine(orderLine);
        }
        order.status = OrderStatus.PENDING;
        order.orderDate = LocalDateTime.now();
        return order;
    }

    public void placed() {
        this.status = OrderStatus.PLACED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCEL;
    }

    public boolean isCancel() {
        return this.status == OrderStatus.CANCEL;
    }

    public OrderDto toOrderDto() {
        List<OrderLineDto> orderLineDtoList = orderLines.stream()
                .map(OrderLine::toOrderLineDto)
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(this.id)
                .userId(this.userId)
                .orderStatus(this.status)
                .deliveryDto(delivery.toDeliveryDto())
                .orderLineDtoList(orderLineDtoList)
                .build();
    }


}
