package com.ecommerce.catalogservice.domain.catalog;

import com.ecommerce.catalogservice.domain.category.CategoryDto;
import com.ecommerce.catalogservice.domain.category.CategoryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
}
