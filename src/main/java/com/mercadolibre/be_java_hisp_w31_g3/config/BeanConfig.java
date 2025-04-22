package com.mercadolibre.be_java_hisp_w31_g3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public ObjectMapper getMapper(){
        return new ObjectMapper();
    }
}
