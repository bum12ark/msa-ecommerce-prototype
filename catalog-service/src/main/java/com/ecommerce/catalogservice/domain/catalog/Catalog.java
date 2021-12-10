package com.ecommerce.catalogservice.domain.catalog;

import com.ecommerce.catalogservice.domain.category.Category;
import com.ecommerce.catalogservice.entity.BaseEntity;
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

    public Optional<CatalogDto> toCatalogDto() {
        return Optional.of(new CatalogDto(name, price, stockQuantity));
    }
}
