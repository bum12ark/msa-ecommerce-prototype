package com.ecommerce.orderservice.domain.order.respository;

import com.ecommerce.orderservice.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
