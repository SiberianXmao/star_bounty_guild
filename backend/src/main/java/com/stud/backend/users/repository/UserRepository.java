package com.stud.backend.users.repository;

import com.stud.backend.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query(value = """
            select
                exists(select 1 from bounty.reviews where author_id = :userId or target_user_id = :userId)
                or exists(select 1 from bounty.complaints where author_id = :userId or target_user_id = :userId)
                or exists(select 1 from bounty.chat_messages where sender_id = :userId)
                or exists(select 1 from bounty.chat_participants where user_id = :userId)
                or exists(select 1 from bounty.favorites where user_id = :userId or target_user_id = :userId)
            """, nativeQuery = true)
    boolean hasPermanentDeleteBlockers(@Param("userId") UUID userId);
}
