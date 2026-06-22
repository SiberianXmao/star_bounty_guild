package com.stud.user.dictionary.client;

import feign.Request;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableFeignClients(basePackageClasses = DictionaryServiceFeignClient.class)
public class DictionaryClientConfiguration {

    @Bean
    Request.Options dictionaryFeignOptions() {
        return new Request.Options(
                2,
                TimeUnit.SECONDS,
                5,
                TimeUnit.SECONDS,
                true
        );
    }
}
