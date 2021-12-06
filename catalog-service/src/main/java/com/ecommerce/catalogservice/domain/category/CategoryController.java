package com.ecommerce.catalogservice.domain.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category")
    public ResponseEntity<CategoryDto> getAllCategory() {

        CategoryDto categoryAll = categoryService.findCategoryAll();

        return ResponseEntity.status(HttpStatus.OK).body(categoryAll);
    }

}
