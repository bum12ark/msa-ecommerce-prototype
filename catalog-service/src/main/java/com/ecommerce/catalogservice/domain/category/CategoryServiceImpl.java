package com.ecommerce.catalogservice.domain.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto findCategoryAll() {
        Map<Long, List<CategoryDto>> groupingByParent = categoryRepository.findAll()
                .stream()
                .filter(category -> Objects.nonNull(category.getParent()))
                .map(category -> new CategoryDto(category.getId(), category.getName()
                        , category.getParent().getId()))
                .collect(Collectors.groupingBy(CategoryDto::getParentId));

        CategoryDto rootNode = CategoryDto.createRootNode();
        addSubCategories(rootNode, groupingByParent);

        return rootNode;
    }

    private void addSubCategories(CategoryDto parent, Map<Long, List<CategoryDto>> groupingByParent) {
        List<CategoryDto> subCategories = groupingByParent.get(parent.getCategoryId());

        // 종료 조건
        if (subCategories == null) return;

        parent.setSubCategories(subCategories);

        // 재귀 호출
        subCategories.forEach(categoryDto -> addSubCategories(categoryDto, groupingByParent));
    }

    @Override
    public CategoryDto findParentCategories(Long categoryId) {
        Map<Long, CategoryDto> categoryIdMap = categoryRepository.findAll()
                .stream()
                .filter(category -> Objects.nonNull(category.getParent()))
                .map(category -> new CategoryDto(category.getId(), category.getName()
                        , category.getParent().getId()))
                .collect(Collectors.toMap(CategoryDto::getCategoryId, Function.identity()));

        return getCategoryLabel(categoryId, categoryIdMap);
    }

    private CategoryDto getCategoryLabel(Long leafCategoryId, Map<Long, CategoryDto> categoryIdMap) {
        CategoryDto categoryDto = categoryIdMap.get(leafCategoryId);

        while (categoryIdMap.get(categoryDto.getParentId()) != null) {
            CategoryDto parent = categoryIdMap.get(categoryDto.getParentId());
            parent.addSubCategories(categoryDto);
            categoryDto = parent;
        }

        CategoryDto rootNode = CategoryDto.createRootNode();
        rootNode.getSubCategories().add(categoryDto);
        return rootNode;
    }

    @Override
    public CategoryDto findById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(NoSuchElementException::new);
        return category.toCategoryDto();
    }
}
