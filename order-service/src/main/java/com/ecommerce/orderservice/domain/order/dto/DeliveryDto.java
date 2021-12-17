package com.ecommerce.orderservice.domain.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DeliveryDto {
    private String city;
    private String street;
    private String zipcode;

    @Builder
    public DeliveryDto(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
