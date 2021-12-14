package com.ecommerce.catalogservice.domain.catalog;

import lombok.*;

@Getter @AllArgsConstructor @EqualsAndHashCode
public class CatalogSearchCondition {
    private Long categoryId;
    private String catalogName;
}
