package com.stud.orders.reviews.web;

import com.stud.orders.reviews.service.HunterReviewService;
import com.stud.orders.reviews.web.dto.ReviewDtos.ReviewCreateRequest;
import com.stud.orders.reviews.web.dto.ReviewDtos.ReviewPageResponse;
import com.stud.orders.reviews.web.dto.ReviewDtos.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HunterReviewController {

    private final HunterReviewService reviewService;

    @PostMapping("/orders/{orderId}/review")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(
            Authentication authentication,
            @PathVariable UUID orderId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        return reviewService.create(authentication.getName(), orderId, request);
    }

    @GetMapping("/reviews/my/client")
    public List<ReviewResponse> getMyClientReviews(Authentication authentication) {
        return reviewService.getMyClientReviews(authentication.getName());
    }

    @GetMapping("/reviews/hunters/{hunterProfileId}")
    public ReviewPageResponse getHunterReviews(
            @PathVariable UUID hunterProfileId,
            Pageable pageable
    ) {
        return reviewService.getHunterReviews(hunterProfileId, pageable);
    }
}
