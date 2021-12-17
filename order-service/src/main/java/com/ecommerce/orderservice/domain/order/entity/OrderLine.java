package com.ecommerce.orderservice.domain.order.entity;

import com.ecommerce.orderservice.domain.order.dto.OrderLineDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderLine {

    @Id @GeneratedValue
    @Column(name = "order_line_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long catalogId;

    private int orderPrice;
    private int count;

    public void setOrder(Order order) {
        this.order = order;
    }

    // == 생성 메소드 == //
    public static OrderLine createOrderLine(int orderPrice, int count, Long catalogId) {
        OrderLine orderLine = new OrderLine();
        orderLine.catalogId = catalogId;

        orderLine.orderPrice = orderPrice;
        orderLine.count = count;

        return orderLine;
    }

    public OrderLineDto toOrderLineDto() {
        return OrderLineDto.builder()
                .catalogId(catalogId).count(count).orderPrice(orderPrice)
                .build();
    }
}
