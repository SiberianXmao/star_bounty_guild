package com.stud.user.users.service;

import com.stud.user.users.domain.enums.RoleName;
import com.stud.user.users.web.dto.UserDtos.AssignRoleRequest;
import com.stud.user.users.web.dto.UserDtos.RoleResponse;
import com.stud.user.users.web.dto.UserDtos.UserCreateRequest;
import com.stud.user.users.web.dto.UserDtos.UserResponse;
import com.stud.user.users.web.dto.UserDtos.UserUpdateStatusRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> getUsers();

    UserResponse getUser(UUID userId);

    List<RoleResponse> getRoles();

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateUserStatus(UUID userId, UserUpdateStatusRequest request);

    void deleteUserPermanently(UUID userId, String currentAdminEmail);

    UserResponse moderateUserStatus(UUID userId, UserUpdateStatusRequest request);

    UserResponse assignRole(UUID userId, AssignRoleRequest request);

    UserResponse removeRole(UUID userId, RoleName roleName);
}
