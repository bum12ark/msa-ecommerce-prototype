package com.ecommerce.orderservice.domain.order.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", url = "127.0.0.1:8000/user-service")
public interface UserClient {

    @GetMapping("/users/userId/{userId}")
    ResponseUser getUser(@PathVariable("userId") Long userId);
}
