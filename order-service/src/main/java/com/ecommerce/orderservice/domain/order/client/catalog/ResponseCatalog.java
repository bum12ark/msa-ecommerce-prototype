package com.ecommerce.orderservice.domain.order.client.catalog;

import lombok.Data;
import lombok.Getter;

@Data
public class ResponseCatalog {
    private Long catalogId;
    private String name;
    private Integer price;
    private Integer stockQuantity;
}
