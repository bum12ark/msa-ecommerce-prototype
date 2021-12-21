package com.ecommerce.catalogservice.domain.catalog.service;

import com.ecommerce.catalogservice.domain.catalog.dto.CatalogCategoryDto;
import com.ecommerce.catalogservice.domain.catalog.dto.CatalogDto;
import com.ecommerce.catalogservice.domain.catalog.dto.CatalogSearchCondition;

import java.util.List;

public interface CatalogService {

    CatalogDto createCatalog(CatalogDto catalogDto, Long categoryId);

    List<CatalogCategoryDto> findCatalogSearch(CatalogSearchCondition condition, Long lastCatalogId);

    List<CatalogDto> findCatalogIn(List<Long> catalogIds);

    CatalogDto getCatalogByCatalogId(Long catalogId);
}
