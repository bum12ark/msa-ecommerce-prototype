package com.ecommerce.catalogservice.domain.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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


}
