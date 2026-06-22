package com.stud.user.users.api;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UserLookup {

    UserRef getByEmail(String email);

    UserRef getById(UUID userId);

    List<UserRef> getByIds(Collection<UUID> userIds);
}
