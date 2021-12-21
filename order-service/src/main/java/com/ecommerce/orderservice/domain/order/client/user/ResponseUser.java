package com.ecommerce.orderservice.domain.order.client.user;

import lombok.Data;

@Data
public class ResponseUser {
    private String email;
    private String name;
    private String city;
    private String street;
    private String zipcode;
    private MemberType memberType;
}
