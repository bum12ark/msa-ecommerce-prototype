package com.ecommerce.catalogservice.domain.order.dto;

import lombok.Data;

@Data
public class DeliveryDto {
    private String city;
    private String street;
    private String zipcode;
}
