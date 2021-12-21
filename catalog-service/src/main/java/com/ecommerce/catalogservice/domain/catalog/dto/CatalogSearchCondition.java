package com.ecommerce.catalogservice.domain.catalog.dto;

import lombok.*;

@Getter @AllArgsConstructor @EqualsAndHashCode
public class CatalogSearchCondition {
    private Long categoryId;
    private String catalogName;
}
