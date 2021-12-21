package com.ecommerce.orderservice.global.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class AppConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
