package com.stud.orders.integrations.users;

public interface UserLookup {

    UserRef getByEmail(String email);
}
