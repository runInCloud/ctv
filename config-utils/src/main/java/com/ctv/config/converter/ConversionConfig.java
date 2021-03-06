package com.ctv.config.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;

/**
 * @author Timur Yarosh
 */
@Configuration
class ConversionConfig {

    @Bean
    public static ConverterRegistrarBeanPostProcessor registrarBeanPostProcessor(ConversionService conversionService) {
        return new ConverterRegistrarBeanPostProcessor((ConverterRegistry) conversionService);
    }

}
