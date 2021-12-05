package com.ecommerce.catalogservice.domain.catalog;

import javax.persistence.Entity;

@Entity
public class SportWear extends Catalog {

    private SportWearSizeEnum size; // 사이즈
}
