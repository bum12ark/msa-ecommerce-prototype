package com.ecommerce.catalogservice.domain.catalog;

import com.ecommerce.catalogservice.domain.category.Category;
import com.ecommerce.catalogservice.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService{

    private final CategoryRepository categoryRepository;
    private final CatalogRepository catalogRepository;
    private final CatalogRepositoryCustom catalogRepositoryCustom;

    @Override
    @Transactional
    public CatalogDto createCatalog(CatalogDto catalogDto, Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(NotExistCategoryException::new);

        Catalog catalog = catalogDto.toEntity();
        catalog.addCategory(category);

        Catalog savedCatalog = catalogRepository.save(catalog);

        return savedCatalog.toCatalogDto()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<CatalogCategoryDto> findCatalogSearch(CatalogSearchCondition condition, Long lastCatalogId) {
        return catalogRepositoryCustom.findMainCatalogsNoOffset(condition, lastCatalogId);
    }

}
