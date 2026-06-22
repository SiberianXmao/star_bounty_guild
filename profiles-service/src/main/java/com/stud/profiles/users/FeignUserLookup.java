package com.stud.profiles.users;

import com.stud.profiles.common.exception.ResourceNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.stud.profiles.users.UserServiceFeignClient.UserIdsRequest;

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

    @Override
    public UserRef getById(UUID userId) {
        try {
            return client.getById(userId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
    }

    @Override
    public List<UserRef> getByIds(Collection<UUID> userIds) {
        return client.getByIds(new UserIdsRequest(List.copyOf(userIds)));
    }
}
