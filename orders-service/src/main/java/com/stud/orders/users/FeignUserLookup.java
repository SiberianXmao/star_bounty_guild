package com.stud.orders.users;

import com.stud.orders.common.exception.ResourceNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeignUserLookup implements UserLookup {

    private final UserServiceFeignClient client;

    @Override
    public UserRef getByEmail(String email) {
        try {
            return client.getByEmail(email);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("User not found: " + email);
        }
    }
}
