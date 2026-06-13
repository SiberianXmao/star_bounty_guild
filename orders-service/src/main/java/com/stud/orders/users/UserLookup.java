package com.stud.orders.users;

public interface UserLookup {

    UserRef getByEmail(String email);
}
