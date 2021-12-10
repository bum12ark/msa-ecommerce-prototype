package com.ecommerce.catalogservice.domain.catalog;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter @EqualsAndHashCode
public class CatalogDto {
    @NotNull
    private String name;
    @NotNull
    private Integer price;
    @NotNull
    private Integer stockQuantity;

    @Builder
    public CatalogDto(String name, Integer price, Integer stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Catalog toEntity() {
        return Catalog.builder()
                .name(name).price(price).stockQuantity(stockQuantity)
                .build();
    }

}
