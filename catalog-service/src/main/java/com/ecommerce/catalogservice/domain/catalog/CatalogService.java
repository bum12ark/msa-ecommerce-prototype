package com.ecommerce.catalogservice.domain.catalog;

import java.util.List;

public interface CatalogService {

    CatalogDto createCatalog(CatalogDto catalogDto, Long categoryId);

    List<CatalogCategoryDto> findCatalogSearch(CatalogSearchCondition condition, Long lastCatalogId);

    List<CatalogDto> findCatalogIn(List<Long> catalogIds);
}
