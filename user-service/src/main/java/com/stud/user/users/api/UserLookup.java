package com.stud.user.users.api;

public interface UserLookup {

    UserRef getByEmail(String email);
}
