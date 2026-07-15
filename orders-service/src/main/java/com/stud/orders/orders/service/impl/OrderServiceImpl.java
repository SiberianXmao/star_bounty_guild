package com.stud.orders.orders.service.impl;

import com.stud.orders.common.exception.ResourceNotFoundException;
import com.stud.orders.integrations.profiles.ClientProfileRef;
import com.stud.orders.integrations.profiles.HunterProfileRef;
import com.stud.orders.integrations.profiles.ProfileProgressUpdater;
import com.stud.orders.orders.domain.BountyOrder;
import com.stud.orders.orders.domain.enums.AcceptanceMode;
import com.stud.orders.orders.domain.enums.OrderStatus;
import com.stud.orders.orders.domain.enums.RiskLevel;
import com.stud.orders.orders.domain.enums.UrgencyLevel;
import com.stud.orders.orders.repository.BountyOrderRepository;
import com.stud.orders.orders.service.OrderService;
import com.stud.orders.orders.service.support.OrderAccessPolicy;
import com.stud.orders.orders.service.support.OrderDraftEditor;
import com.stud.orders.orders.service.support.OrderReferenceResolver;
import com.stud.orders.orders.service.support.OrderResponseAssembler;
import com.stud.orders.orders.service.support.OrderSearchSpecBuilder;
import com.stud.orders.orders.web.dto.OrderDtos.OrderCreateRequest;
import com.stud.orders.orders.web.dto.OrderDtos.OrderResponse;
import com.stud.orders.orders.web.dto.OrderDtos.OrderUpdateDraftRequest;
import com.stud.orders.orders.web.dto.OrderDtos.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final BountyOrderRepository bountyOrderRepository;
    private final ProfileProgressUpdater profileProgressUpdater;

    private final OrderReferenceResolver references;
    private final OrderAccessPolicy accessPolicy;
    private final OrderDraftEditor draftEditor;
    private final OrderResponseAssembler responses;
    private final OrderSearchSpecBuilder searchSpecs;

    @Override
    @Transactional
    public OrderResponse createDraft(String email, OrderCreateRequest request) {
        ClientProfileRef clientProfile = references.currentClientProfile(email);
        BountyOrder order = draftEditor.createDraft(clientProfile, request);

        return responses.toResponse(bountyOrderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse updateDraft(String email, UUID orderId, OrderUpdateDraftRequest request) {
        ClientProfileRef clientProfile = references.currentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        accessPolicy.ensureOwner(order, clientProfile);
        accessPolicy.ensureDraft(order);
        draftEditor.updateDraft(order, request);

        return responses.toResponse(bountyOrderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse publish(String email, UUID orderId) {
        ClientProfileRef clientProfile = references.currentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        accessPolicy.ensureOwner(order, clientProfile);
        accessPolicy.ensureDraft(order);
        accessPolicy.ensurePublishable(order);

        order.setStatus(OrderStatus.OPEN);
        order.setPublishedAt(Instant.now());

        return responses.toResponse(bountyOrderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse cancel(String email, UUID orderId) {
        ClientProfileRef clientProfile = references.currentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        accessPolicy.ensureOwner(order, clientProfile);
        accessPolicy.ensureCancellable(order);

        order.setStatus(OrderStatus.CANCELLED);

        return responses.toResponse(bountyOrderRepository.save(order));
    }

    @Override
    public PageResponse<OrderResponse> getMyHunterOrders(String email, Pageable pageable) {
        HunterProfileRef hunterProfile = references.currentHunterProfile(email);

        Page<BountyOrder> page = bountyOrderRepository.findAllByAssignedHunterId(hunterProfile.id(), pageable);

        return responses.toPageResponse(page);
    }

    @Override
    public OrderResponse getMyHunterOrder(String email, UUID orderId) {
        HunterProfileRef hunterProfile = references.currentHunterProfile(email);
        BountyOrder order = findOrder(orderId);

        accessPolicy.ensureAssignedHunter(order, hunterProfile);

        return responses.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse startOrder(String email, UUID orderId) {
        HunterProfileRef hunterProfile = references.currentHunterProfile(email);
        BountyOrder order = findOrder(orderId);

        accessPolicy.ensureAssignedHunter(order, hunterProfile);
        accessPolicy.ensureCanStart(order);

        order.setStatus(OrderStatus.IN_PROGRESS);

        return responses.toResponse(bountyOrderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse submitOrder(String email, UUID orderId) {
        HunterProfileRef hunterProfile = references.currentHunterProfile(email);
        BountyOrder order = findOrder(orderId);

        accessPolicy.ensureAssignedHunter(order, hunterProfile);
        accessPolicy.ensureCanSubmit(order);

        order.setStatus(OrderStatus.SUBMITTED);

        return responses.toResponse(bountyOrderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse completeOrder(String email, UUID orderId) {
        ClientProfileRef clientProfile = references.currentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        accessPolicy.ensureOwner(order, clientProfile);
        accessPolicy.ensureCanComplete(order);

        UUID clientProfileId = order.getClientId();
        UUID hunterProfileId = accessPolicy.requireAssignedHunterId(order);

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(Instant.now());

        profileProgressUpdater.recordCompletedOrder(clientProfileId, hunterProfileId);

        return responses.toResponse(bountyOrderRepository.save(order));
    }

    @Override
    public PageResponse<OrderResponse> getPublicOrders(
            UUID categoryId,
            UUID planetId,
            UUID sectorId,
            RiskLevel riskLevel,
            UrgencyLevel urgencyLevel,
            AcceptanceMode acceptanceMode,
            BigDecimal rewardMin,
            BigDecimal rewardMax,
            String q,
            Pageable pageable
    ) {
        Specification<BountyOrder> spec = searchSpecs.publicBoard(
                categoryId,
                planetId,
                sectorId,
                riskLevel,
                urgencyLevel,
                acceptanceMode,
                rewardMin,
                rewardMax,
                q
        );

        Page<BountyOrder> page = bountyOrderRepository.findAll(spec, pageable);

        return responses.toPageResponse(page);
    }

    @Override
    public OrderResponse getPublicOrder(UUID orderId) {
        BountyOrder order = findOrder(orderId);

        accessPolicy.ensurePublicBoardVisible(order, orderId);

        return responses.toResponse(order);
    }

    @Override
    public PageResponse<OrderResponse> getMyClientOrders(String email, Pageable pageable) {
        ClientProfileRef clientProfile = references.currentClientProfile(email);

        Page<BountyOrder> page = bountyOrderRepository.findAllByClientId(clientProfile.id(), pageable);

        return responses.toPageResponse(page);
    }

    @Override
    public OrderResponse getMyClientOrder(String email, UUID orderId) {
        ClientProfileRef clientProfile = references.currentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        accessPolicy.ensureOwner(order, clientProfile);

        return responses.toResponse(order);
    }

    private BountyOrder findOrder(UUID orderId) {
        return bountyOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

}
