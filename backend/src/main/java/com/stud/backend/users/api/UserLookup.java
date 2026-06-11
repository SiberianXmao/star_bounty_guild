package com.stud.backend.users.api;

public interface UserLookup {

    UserRef getByEmail(String email);
}
