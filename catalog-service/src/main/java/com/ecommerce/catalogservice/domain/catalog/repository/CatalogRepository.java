package com.ecommerce.catalogservice.domain.catalog.repository;

import com.ecommerce.catalogservice.domain.catalog.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    List<Catalog> findByIdIn(List<Long> catalogIds);
}
