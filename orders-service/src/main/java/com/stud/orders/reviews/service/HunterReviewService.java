package com.stud.orders.reviews.service;

import com.stud.orders.common.exception.BadRequestException;
import com.stud.orders.common.exception.DuplicateResourceException;
import com.stud.orders.common.exception.ResourceNotFoundException;
import com.stud.orders.orders.domain.BountyOrder;
import com.stud.orders.orders.domain.enums.OrderStatus;
import com.stud.orders.orders.repository.BountyOrderRepository;
import com.stud.orders.profiles.ClientProfileRef;
import com.stud.orders.profiles.HunterProfileRef;
import com.stud.orders.profiles.ProfileLookup;
import com.stud.orders.reviews.api.HunterRatedEvent;
import com.stud.orders.reviews.domain.HunterReview;
import com.stud.orders.reviews.repository.HunterReviewRepository;
import com.stud.orders.reviews.web.dto.ReviewDtos.ReviewCreateRequest;
import com.stud.orders.reviews.web.dto.ReviewDtos.ReviewPageResponse;
import com.stud.orders.reviews.web.dto.ReviewDtos.ReviewResponse;
import com.stud.orders.users.UserLookup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HunterReviewService {

    private final HunterReviewRepository reviewRepository;
    private final BountyOrderRepository orderRepository;
    private final UserLookup userLookup;
    private final ProfileLookup profileLookup;
    private final HunterReviewEventPublisher eventPublisher;

    @Transactional
    public ReviewResponse create(String email, UUID orderId, ReviewCreateRequest request) {
        UUID userId = userLookup.getByEmail(email).id();
        ClientProfileRef client = profileLookup.getClientProfileByUserId(userId);
        BountyOrder order = findOrder(orderId);

        if (!order.getClientId().equals(client.id())) {
            throw new ResourceNotFoundException("Order not found: " + orderId);
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new BadRequestException("A review can be submitted only after the order is completed");
        }

        if (order.getAssignedHunterId() == null) {
            throw new BadRequestException("Completed order has no assigned hunter");
        }

        if (reviewRepository.existsByOrderId(orderId)) {
            throw new DuplicateResourceException("A review for this order already exists");
        }

        HunterReview review = new HunterReview();
        review.setOrderId(orderId);
        review.setClientProfileId(client.id());
        review.setHunterProfileId(order.getAssignedHunterId());
        review.setRating(request.rating());
        review.setComment(normalizeComment(request.comment()));

        HunterReview savedReview;
        try {
            savedReview = reviewRepository.saveAndFlush(review);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException("A review for this order already exists");
        }
        eventPublisher.publish(new HunterRatedEvent(
                savedReview.getId(),
                orderId,
                savedReview.getHunterProfileId(),
                savedReview.getRating(),
                savedReview.getCreatedAt() == null ? Instant.now() : savedReview.getCreatedAt()
        ));

        HunterProfileRef hunter = profileLookup.getHunterProfile(savedReview.getHunterProfileId());
        return toResponse(savedReview, order, client, hunter);
    }

    public List<ReviewResponse> getMyClientReviews(String email) {
        UUID userId = userLookup.getByEmail(email).id();
        ClientProfileRef client = profileLookup.getClientProfileByUserId(userId);
        List<HunterReview> reviews = reviewRepository.findAllByClientProfileIdOrderByCreatedAtDesc(client.id());
        return mapResponses(reviews);
    }

    public ReviewPageResponse getHunterReviews(UUID hunterProfileId, Pageable pageable) {
        profileLookup.getHunterProfile(hunterProfileId);
        Page<HunterReview> reviews = reviewRepository
                .findAllByHunterProfileIdOrderByCreatedAtDesc(hunterProfileId, pageable);
        List<ReviewResponse> content = mapResponses(reviews.getContent());

        return new ReviewPageResponse(
                content,
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements(),
                reviews.getTotalPages(),
                reviews.isFirst(),
                reviews.isLast()
        );
    }

    private List<ReviewResponse> mapResponses(List<HunterReview> reviews) {
        if (reviews.isEmpty()) {
            return List.of();
        }

        Map<UUID, BountyOrder> orders = orderRepository.findAllById(
                        reviews.stream().map(HunterReview::getOrderId).toList()
                ).stream()
                .collect(Collectors.toMap(BountyOrder::getId, Function.identity()));

        Map<UUID, ClientProfileRef> clients = profileLookup.getClientProfilesByIds(
                reviews.stream().map(HunterReview::getClientProfileId).toList()
        );
        Map<UUID, HunterProfileRef> hunters = profileLookup.getHunterProfilesByIds(
                reviews.stream().map(HunterReview::getHunterProfileId).toList()
        );

        return reviews.stream()
                .map(review -> toResponse(
                        review,
                        orders.get(review.getOrderId()),
                        clients.get(review.getClientProfileId()),
                        hunters.get(review.getHunterProfileId())
                ))
                .toList();
    }

    private ReviewResponse toResponse(
            HunterReview review,
            BountyOrder order,
            ClientProfileRef client,
            HunterProfileRef hunter
    ) {
        return new ReviewResponse(
                review.getId(),
                review.getOrderId(),
                order == null ? null : order.getTitle(),
                review.getClientProfileId(),
                client == null ? null : client.name(),
                review.getHunterProfileId(),
                hunter == null ? null : hunter.callsign(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    private BountyOrder findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    private String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return null;
        }
        return comment.trim();
    }
}
