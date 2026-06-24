package com.stud.dictionary.media.client;

import com.stud.dictionary.internal.auth.InternalAuthProperties;
import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableFeignClients(basePackageClasses = FileServiceFeignClient.class)
public class FileClientConfiguration {

    @Bean
    RequestInterceptor fileInternalAuthInterceptor(InternalAuthProperties properties) {
        return template -> {
            if (StringUtils.hasText(properties.getToken())) {
                template.header(properties.getHeaderName(), properties.getToken());
            }
        };
    }
}
