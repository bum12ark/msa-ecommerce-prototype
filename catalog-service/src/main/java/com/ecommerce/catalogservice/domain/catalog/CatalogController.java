package com.ecommerce.catalogservice.domain.catalog;

import com.ecommerce.catalogservice.domain.category.CategoryDto;
import com.ecommerce.catalogservice.domain.category.CategoryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;
    private final CategoryService categoryService;

    @PostMapping("/catalog")
    public ResponseEntity<ResponseCatalog> createCatalog(@RequestBody @Valid RequestCatalog requestCatalog) {

        CatalogDto catalogDto =
                catalogService.createCatalog(requestCatalog.toCreateCatalogDto(), requestCatalog.getCategoryId());

        CategoryDto categoryDto = categoryService.findById(requestCatalog.getCategoryId());

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseCatalog(catalogDto, categoryDto));
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    static class RequestCatalog {
        @NotNull
        private String name;
        @NotNull @Min(0)
        private Integer price;
        @NotNull @Min(0)
        private Integer stockQuantity;
        @NotNull
        private Long categoryId;

        public CatalogDto toCreateCatalogDto() {
            return new CatalogDto(name, price, stockQuantity);
        }
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    static class ResponseCatalog {
        private String name;
        private Integer price;
        private Integer stockQuantity;
        private Long categoryId;
        private String categoryName;

        public ResponseCatalog(CatalogDto catalogDto, CategoryDto categoryDto) {
            this.name = catalogDto.getName();
            this.price = catalogDto.getPrice();
            this.stockQuantity = catalogDto.getStockQuantity();
            this.categoryId = categoryDto.getCategoryId();
            this.categoryName = categoryDto.getName();
        }
    }

    @GetMapping("/catalog")
    public ResponseEntity getMainCatalogs(CatalogSearchCondition condition,
                                          @RequestParam(value = "lastCatalogId", required = false)
                                                 Long lastCatalogId) {

        List<CatalogCategoryDto> catalogSearch = catalogService.findCatalogSearch(condition, lastCatalogId);
        List<ResponseCatalogMain> responses = catalogSearch
                .stream()
                .map(ResponseCatalogMain::new)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Result<>(responses.size(), responses));
    }

    @Data @AllArgsConstructor
    static class Result<T> {
        private Integer count;
        private T data;
    }

    @Data @AllArgsConstructor
    static class ResponseCatalogMain {
        private Long catalogId;
        private String catalogName;
        private Integer price;
        private Integer stockQuantity;
        private Long categoryId;

        public ResponseCatalogMain(CatalogCategoryDto dto) {
            this.catalogId = dto.getCatalogId();
            this.catalogName = dto.getCatalogName();
            this.price = dto.getPrice();
            this.stockQuantity = dto.getStockQuantity();
            this.categoryId = dto.getCategoryId();
        }
    }
}
