package com.stud.backend.internal;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class InternalFeignConfiguration {

    @Bean
    RequestInterceptor internalAuthRequestInterceptor(InternalAuthProperties properties) {
        return template -> {
            if (StringUtils.hasText(properties.getToken())) {
                template.header(properties.getHeaderName(), properties.getToken());
            }
        };
    }
}
