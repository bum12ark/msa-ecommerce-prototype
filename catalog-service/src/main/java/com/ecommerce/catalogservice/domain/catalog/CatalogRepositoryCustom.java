package com.ecommerce.catalogservice.domain.catalog;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ecommerce.catalogservice.domain.catalog.QCatalog.*;
import static com.ecommerce.catalogservice.domain.category.QCategory.*;
import static org.springframework.util.StringUtils.hasText;


@Repository
@RequiredArgsConstructor
public class CatalogRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /*
    SELECT *
    FROM items
    WHERE 조건문
    AND id < 마지막 조회 ID
    ORDER BY id desc
    LIMIT 페이지 사이즈
     */
    public List<CatalogCategoryDto> findMainCatalogsNoOffset(CatalogSearchCondition condition, Long lastCatalogId) {
        return queryFactory
                .select(Projections.constructor(
                        CatalogCategoryDto.class,
                        catalog.id, catalog.name, catalog.price, catalog.stockQuantity, category.id
                ))
                .from(catalog)
                .join(catalog.category, category)
                .where(
                        catalogNameContains(condition.getCatalogName()),
                        categoryIdEq(condition.getCategoryId()),
                        catalogIdLt(lastCatalogId)
                )
                .orderBy(catalog.id.desc())
                .limit(10)
                .fetch();
    }

    private BooleanExpression catalogIdLt(Long lastCatalogId) {
        return lastCatalogId != null ? catalog.id.lt(lastCatalogId) : null;
    }

    private BooleanExpression catalogNameContains(String catalogName) {
        return hasText(catalogName) ? catalog.name.contains(catalogName) : null;
    }

    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? category.id.eq(categoryId) : null;
    }
}
