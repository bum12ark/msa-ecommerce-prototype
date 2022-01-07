package com.ecommerce.orderservice.domain.order.respository;

import com.ecommerce.orderservice.domain.order.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.ecommerce.orderservice.domain.order.entity.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Optional<Order> findMainOrderById(Long orderId) {
        Order findOrder = queryFactory
                .selectFrom(order)
                .join(order.orderLines).fetchJoin()
                .join(order.delivery).fetchJoin()
                .distinct()
                .fetchOne();
        return Optional.ofNullable(findOrder);
    }
}
