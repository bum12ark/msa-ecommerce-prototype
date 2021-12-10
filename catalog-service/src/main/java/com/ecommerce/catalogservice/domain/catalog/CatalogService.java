package com.ecommerce.catalogservice.domain.catalog;

public interface CatalogService {

    CatalogDto createCatalog(CatalogDto catalogDto, Long categoryId);

}
