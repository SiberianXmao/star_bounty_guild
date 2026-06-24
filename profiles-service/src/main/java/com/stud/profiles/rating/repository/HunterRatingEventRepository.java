package com.stud.profiles.rating.repository;

import com.stud.profiles.rating.domain.HunterRatingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HunterRatingEventRepository extends JpaRepository<HunterRatingEvent, UUID> {
}
