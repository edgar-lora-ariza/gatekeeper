package com.white.label.gatekeeper.infrastructure.config;

import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class ConversionConfig {

  @Bean
  public ConversionService conversionService(Set<Converter<?, ?>> converters) {
    ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
    factory.setConverters(converters);
    factory.afterPropertiesSet();
    return factory.getObject();
  }

}