package com.ecommerce.catalogservice.domain.category;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryDto {
    public static final long ROOT = 0;

    private Long categoryId;
    private String name;
    private Long parentId;
    private List<CategoryDto> subCategories = new ArrayList<>();

    public CategoryDto(Long categoryId, String name, Long parentId) {
        this.categoryId = categoryId;
        this.name = name;
        this.parentId = parentId;
    }

    public static CategoryDto createRootNode() {
        return new CategoryDto(ROOT, "홈", null);
    }

    public void addSubCategories(CategoryDto categoryDto) {
        this.subCategories.add(categoryDto);
    }
}
