package com.ecommerce.catalogservice.domain.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CatalogCategoryDto {

    private Long catalogId;
    private String catalogName;
    private Integer price;
    private Integer stockQuantity;

    private Long categoryId;
}
