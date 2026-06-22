package com.stud.orders.config;

import com.stud.orders.internal.auth.InternalAuthProperties;
import feign.Request;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfiguration {

    @Bean
    Request.Options feignOptions() {
        return new Request.Options(
                2,
                TimeUnit.SECONDS,
                5,
                TimeUnit.SECONDS,
                true
        );
    }

    @Bean
    RequestInterceptor internalAuthRequestInterceptor(InternalAuthProperties properties) {
        return template -> {
            if (StringUtils.hasText(properties.getToken())) {
                template.header(properties.getHeaderName(), properties.getToken());
            }
        };
    }
}
