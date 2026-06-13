package com.stud.orders.users;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "user-service",
        url = "${app.services.users.base-url}",
        path = "/internal/v1/users"
)
public interface UserServiceFeignClient {

    @GetMapping("/by-email")
    UserRef getByEmail(@RequestParam String email);
}
