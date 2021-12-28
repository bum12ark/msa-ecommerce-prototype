package com.ecommerce.catalogservice.domain.catalog.entity;

import com.ecommerce.catalogservice.domain.catalog.dto.CatalogDto;
import com.ecommerce.catalogservice.domain.category.entity.Category;
import com.ecommerce.catalogservice.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Catalog extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer price;

    private Integer stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Catalog(String name, Integer price, Integer stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    /** 연관관계 편의 메소드 **/
    public void addCategory(Category category) {
        this.category = category;
    }

    public void decreaseStockQuantity(Integer count) {
        this.stockQuantity -= count;
    }

    public void increaseStockQuantity(Integer count) {
        this.stockQuantity += count;
    }

    public boolean isAvailableStock(Integer count) {
        return this.stockQuantity - count >= 0;
    }

    public Optional<CatalogDto> toOptionalCatalogDto() {
        return Optional.of(new CatalogDto(name, price, stockQuantity));
    }

    public CatalogDto toCatalogDto() {
        return new CatalogDto(id, name, price, stockQuantity);
    }

}
