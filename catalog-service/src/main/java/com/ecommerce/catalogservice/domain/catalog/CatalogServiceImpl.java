package com.ecommerce.catalogservice.domain.catalog;

import com.ecommerce.catalogservice.domain.category.Category;
import com.ecommerce.catalogservice.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

        return savedCatalog.toOptionalCatalogDto()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<CatalogCategoryDto> findCatalogSearch(CatalogSearchCondition condition, Long lastCatalogId) {
        return catalogRepositoryCustom.findMainCatalogsNoOffset(condition, lastCatalogId);
    }

    @Override
    public List<CatalogDto> findCatalogIn(List<Long> catalogIds) {
        return catalogRepository.findByIdIn(catalogIds)
                .stream()
                .map(Catalog::toCatalogDto)
                .collect(Collectors.toList());
    }

    @Override
    public CatalogDto getCatalogByCatalogId(Long catalogId) {
        Catalog catalog = catalogRepository.findById(catalogId)
                .orElseThrow(NotExistCatalogException::new);

        return catalog.toCatalogDto();
    }
}
