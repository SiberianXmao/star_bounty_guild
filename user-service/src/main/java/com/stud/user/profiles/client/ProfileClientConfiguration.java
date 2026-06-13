package com.stud.user.profiles.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = ProfileServiceFeignClient.class)
public class ProfileClientConfiguration {
}
