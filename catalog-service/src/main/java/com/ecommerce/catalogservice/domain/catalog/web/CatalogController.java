package com.ecommerce.catalogservice.domain.catalog.web;

import com.ecommerce.catalogservice.domain.catalog.dto.CatalogCategoryDto;
import com.ecommerce.catalogservice.domain.catalog.dto.CatalogDto;
import com.ecommerce.catalogservice.domain.catalog.dto.CatalogSearchCondition;
import com.ecommerce.catalogservice.domain.catalog.service.CatalogService;
import com.ecommerce.catalogservice.domain.category.dto.CategoryDto;
import com.ecommerce.catalogservice.domain.category.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor @Slf4j
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

    @GetMapping("/catalog/main")
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

    @GetMapping("/catalogs/{catalogIds}")
    public ResponseEntity getCatalogIn(@PathVariable List<Long> catalogIds) {
        log.info("catalogIds = {}", catalogIds);

        List<ResponseCatalogIn> responses = catalogService.findCatalogIn(catalogIds)
                .stream()
                .map(ResponseCatalogIn::new)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Result<>(responses.size(), responses));
    }

    @Data
    static class ResponseCatalogIn {
        @NotNull
        private Long catalogId;
        @NotNull
        private String name;
        @NotNull
        private Integer price;
        @NotNull
        private Integer stockQuantity;

        public ResponseCatalogIn(CatalogDto dto) {
            this.catalogId = dto.getCatalogId();
            this.name = dto.getName();
            this.price = dto.getPrice();
            this.stockQuantity = dto.getStockQuantity();
        }
    }

    @Data @AllArgsConstructor
    static class Result<T> {
        private Integer count;
        private T data;
    }

    @GetMapping("/catalog/{catalogId}")
    public ResponseEntity getCatalog(@PathVariable("catalogId") Long catalogId) {

        CatalogDto catalogByCatalogId = catalogService.getCatalogByCatalogId(catalogId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseGetCatalog(catalogByCatalogId));
    }

    @Data
    static class ResponseGetCatalog {
        private Long catalogId;
        private String name;
        private Integer price;
        private Integer stockQuantity;

        public ResponseGetCatalog(CatalogDto dto) {
           this.catalogId = dto.getCatalogId();
           this.name = dto.getName();
           this.price = dto.getPrice();
           this.stockQuantity = dto.getStockQuantity();
        }
    }
}
