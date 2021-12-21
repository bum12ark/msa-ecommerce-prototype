package com.ecommerce.catalogservice.domain.category.service;

import com.ecommerce.catalogservice.domain.category.dto.CategoryDto;

public interface CategoryService {

    CategoryDto findCategoryAll();

    CategoryDto findParentCategories(Long categoryId);

    CategoryDto findById(Long categoryId);

}
