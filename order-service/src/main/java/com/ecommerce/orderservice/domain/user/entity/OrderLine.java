package com.ecommerce.orderservice.domain.user.entity;

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

    private Long itemId;

    private int orderPrice;
    private int count;

    public void setOrder(Order order) {
        this.order = order;
    }

    // == 생성 메소드 == //
    public static OrderLine createOrderLine(int orderPrice, int count, Long itemId) {
        OrderLine orderLine = new OrderLine();
        orderLine.itemId = itemId;

        orderLine.orderPrice = orderPrice;
        orderLine.count = count;

        // TODO 주문 수량 만큼 재고를 삭제

        return orderLine;
    }
}
