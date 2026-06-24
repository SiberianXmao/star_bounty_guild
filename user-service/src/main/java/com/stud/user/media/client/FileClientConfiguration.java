package com.stud.user.media.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = FileServiceFeignClient.class)
public class FileClientConfiguration {
}
