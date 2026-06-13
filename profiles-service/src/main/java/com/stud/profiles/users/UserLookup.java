package com.stud.profiles.users;

public interface UserLookup {

    UserRef getByEmail(String email);
}
