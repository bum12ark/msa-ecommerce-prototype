package com.ecommerce.catalogservice.domain.category;

public interface CategoryService {

    CategoryDto findCategoryAll();

    CategoryDto findParentCategories(Long categoryId);

}
