package com.ecommerce.catalogservice.domain.catalog.dto;

import com.ecommerce.catalogservice.domain.catalog.entity.Catalog;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter @EqualsAndHashCode
public class CatalogDto {
    @NotNull
    private Long catalogId;
    @NotNull
    private String name;
    @NotNull
    private Integer price;
    @NotNull
    private Integer stockQuantity;

    public CatalogDto(String name, Integer price, Integer stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    @Builder
    public CatalogDto(Long catalogId, String name, Integer price, Integer stockQuantity) {
        this.catalogId = catalogId;
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
