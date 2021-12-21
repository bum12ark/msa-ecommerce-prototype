package com.ecommerce.orderservice.domain.order.client.catalog;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", url = "127.0.0.1:8000/catalog-service")
public interface CatalogClient {

    @GetMapping("/catalog/{catalogId}")
    ResponseCatalog getCatalogById(@PathVariable("catalogId") Long catalogId);
}
