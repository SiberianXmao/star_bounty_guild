package com.stud.profiles.rating.service;

import com.stud.profiles.common.exception.ResourceNotFoundException;
import com.stud.profiles.domain.HunterProfile;
import com.stud.profiles.rating.domain.HunterRatingEvent;
import com.stud.profiles.rating.repository.HunterRatingEventRepository;
import com.stud.profiles.repository.HunterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HunterRatingProjection {

    private final HunterProfileRepository hunterProfileRepository;
    private final HunterRatingEventRepository ratingEventRepository;

    @Transactional
    public void apply(RatingEvent event) {
        if (ratingEventRepository.existsById(event.reviewId())) {
            return;
        }

        HunterProfile hunter = hunterProfileRepository.findByIdForUpdate(event.hunterProfileId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hunter profile not found: " + event.hunterProfileId()
                ));

        int currentCount = hunter.getRatingCount();
        BigDecimal weightedTotal = hunter.getAverageRating().multiply(BigDecimal.valueOf(currentCount));
        int newCount = currentCount + 1;
        BigDecimal newAverage = weightedTotal
                .add(BigDecimal.valueOf(event.rating()))
                .divide(BigDecimal.valueOf(newCount), 2, RoundingMode.HALF_UP);

        HunterRatingEvent processedEvent = new HunterRatingEvent();
        processedEvent.setReviewId(event.reviewId());
        processedEvent.setOrderId(event.orderId());
        processedEvent.setHunterProfileId(event.hunterProfileId());
        processedEvent.setRating(event.rating());
        processedEvent.setCreatedAt(event.createdAt());
        ratingEventRepository.save(processedEvent);

        hunter.setAverageRating(newAverage);
        hunter.setRatingCount(newCount);
    }

    public record RatingEvent(
            UUID reviewId,
            UUID orderId,
            UUID hunterProfileId,
            Integer rating,
            Instant createdAt
    ) {
    }
}
