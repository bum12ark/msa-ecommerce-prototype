package com.ecommerce.orderservice.domain.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DeliveryDto {
    private Long id;
    private String city;
    private String street;
    private String zipcode;

    @Builder
    public DeliveryDto(Long id, String city, String street, String zipcode) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
