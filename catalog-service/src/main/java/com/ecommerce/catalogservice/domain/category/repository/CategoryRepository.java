package com.ecommerce.catalogservice.domain.category.repository;

import com.ecommerce.catalogservice.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
