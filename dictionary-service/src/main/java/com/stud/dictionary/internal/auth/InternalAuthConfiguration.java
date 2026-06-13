package com.stud.dictionary.internal.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(InternalAuthProperties.class)
public class InternalAuthConfiguration implements WebMvcConfigurer {

    private final InternalAuthProperties properties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InternalAuthInterceptor(properties))
                .addPathPatterns("/internal/**");
    }
}
