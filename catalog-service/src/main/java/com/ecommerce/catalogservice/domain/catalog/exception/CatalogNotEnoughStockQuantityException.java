package com.ecommerce.catalogservice.domain.catalog.exception;

public class CatalogNotEnoughStockQuantityException extends RuntimeException {
    private Long catalogId;
    private Integer count;
    private Integer stockQuantity;

    public CatalogNotEnoughStockQuantityException(Long catalogId, Integer count, Integer stockQuantity) {
        super(String.format("catalogId = %s, count = %s, stockQuantity = %s", catalogId, count, stockQuantity));
        this.catalogId = catalogId;
        this.count = count;
        this.stockQuantity = stockQuantity;
    }
}
