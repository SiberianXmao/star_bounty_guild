package com.stud.orders.applications.service;


import com.stud.orders.applications.domain.OrderApplication;
import com.stud.orders.applications.domain.enums.ApplicationStatus;
import com.stud.orders.applications.repository.OrderApplicationRepository;

import com.stud.orders.common.exception.BadRequestException;
import com.stud.orders.common.exception.DuplicateResourceException;
import com.stud.orders.common.exception.ResourceNotFoundException;

import com.stud.orders.applications.api.ApplicationAcceptedEvent;
import com.stud.orders.applications.api.OrderApplicationEventPublisher;

import com.stud.orders.orders.api.OrderLookup;
import com.stud.orders.orders.api.OrderRef;
import com.stud.orders.profiles.ClientProfileRef;
import com.stud.orders.profiles.HunterProfileRef;
import com.stud.orders.profiles.ProfileLookup;

import com.stud.orders.users.UserLookup;
import com.stud.orders.users.UserRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stud.orders.applications.web.dto.OrderApplicationDtos.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderApplicationService {

    private final OrderApplicationRepository orderApplicationRepository;
    private final ProfileLookup profileLookup;
    private final UserLookup userLookup;
    private final OrderLookup orderLookup;
    private final OrderApplicationEventPublisher eventPublisher;

    @Transactional
    public ApplicationResponse applyToOrder(String email, UUID orderId, ApplicationCreateRequest request) {
        HunterProfileRef hunter = findCurrentHunterProfile(email);
        OrderRef order = findOrder(orderId);
        ClientProfileRef orderClient = profileLookup.getClientProfile(order.clientId());

        if (!order.isOpen()) {
            throw new BadRequestException("Only OPEN orders can receive applications");
        }

        if (!order.acceptsApplications()) {
            throw new BadRequestException("This order does not accept applications");
        }

        if (orderClient.userId().equals(hunter.userId())) {
            throw new BadRequestException("You cannot apply to your own order");
        }

        if (orderApplicationRepository.existsByOrderIdAndHunterId(order.id(), hunter.id())) {
            throw new DuplicateResourceException("You have already applied to this order");
        }

        OrderApplication application = new OrderApplication();
        application.setOrderId(order.id());
        application.setHunterId(hunter.id());
        application.setMessage(request.message());
        application.setProposedReward(request.proposedReward());
        application.setStatus(ApplicationStatus.PENDING);

        return toApplicationResponse(orderApplicationRepository.save(application));
    }

    public List<ApplicationResponse> getMyHunterApplications(String email) {
        HunterProfileRef hunter = findCurrentHunterProfile(email);

        List<OrderApplication> applications = orderApplicationRepository.findAllByHunterIdOrderByCreatedAtDesc(hunter.id());

        return toApplicationResponses(applications);
    }

    public List<ApplicationResponse> getApplicationsForMyOrder(String email, UUID orderId) {
        ClientProfileRef client = findCurrentClientProfile(email);
        OrderRef order = findOrder(orderId);

        ensureOrderOwner(order, client);

        List<OrderApplication> applications = orderApplicationRepository.findAllByOrderId(order.id());

        return toApplicationResponses(applications);
    }

    @Transactional
    public ApplicationResponse withdrawMyApplication(String email, UUID applicationId) {
        HunterProfileRef hunter = findCurrentHunterProfile(email);
        OrderApplication application = findApplication(applicationId);

        if (!application.getHunterId().equals(hunter.id())) {
            throw new ResourceNotFoundException("Application not found: " + applicationId);
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only PENDING applications can be withdrawn");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);

        return toApplicationResponse(orderApplicationRepository.save(application));
    }

    @Transactional
    public ApplicationResponse acceptApplication(String email, UUID applicationId) {
        ClientProfileRef client = findCurrentClientProfile(email);
        OrderApplication application = findApplication(applicationId);
        OrderRef order = findOrder(application.getOrderId());

        ensureOrderOwner(order, client);

        if (!order.isOpen()) {
            throw new BadRequestException("Only OPEN orders can accept applications");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only PENDING applications can be accepted");
        }

        application.setStatus(ApplicationStatus.ACCEPTED);

        List<OrderApplication> pendingApplications =
                orderApplicationRepository.findAllByOrderIdAndStatus(application.getOrderId(), ApplicationStatus.PENDING);

        for (OrderApplication otherApplication : pendingApplications) {
            if (!otherApplication.getId().equals(application.getId())) {
                otherApplication.setStatus(ApplicationStatus.REJECTED);
            }
        }



        eventPublisher.publish(new ApplicationAcceptedEvent(
                application.getId(),
                application.getOrderId(),
                application.getHunterId()
        ));
        orderApplicationRepository.saveAll(pendingApplications);

        return toApplicationResponse(orderApplicationRepository.save(application));
    }

    @Transactional
    public ApplicationResponse rejectApplication(String email, UUID applicationId) {
        ClientProfileRef client = findCurrentClientProfile(email);
        OrderApplication application = findApplication(applicationId);
        OrderRef order = findOrder(application.getOrderId());

        ensureOrderOwner(order, client);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only PENDING applications can be rejected");
        }

        application.setStatus(ApplicationStatus.REJECTED);

        return toApplicationResponse(orderApplicationRepository.save(application));
    }

    private UserRef findUserByEmail(String email) {
        return userLookup.getByEmail(email);
    }

    private ClientProfileRef findCurrentClientProfile(String email) {
        UserRef user = findUserByEmail(email);

        return profileLookup.getClientProfileByUserId(user.id());
    }

    private HunterProfileRef findCurrentHunterProfile(String email) {
        UserRef user = findUserByEmail(email);

        return  profileLookup.getHunterProfileByUserId(user.id());
    }

    private OrderRef findOrder(UUID orderId) {
        return orderLookup.getOrder(orderId);
    }

    private OrderApplication findApplication(UUID applicationId) {
        return orderApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));
    }

    private void ensureOrderOwner(OrderRef order, ClientProfileRef client) {
        if (!order.clientId().equals(client.id())) {
            throw new ResourceNotFoundException("Order not found: " + order.id());
        }
    }

    private ApplicationResponse toApplicationResponse(OrderApplication application) {
        HunterProfileRef hunter = profileLookup.getHunterProfile(application.getHunterId());
        OrderRef order = orderLookup.getOrder(application.getOrderId());

        return new ApplicationResponse(
                application.getId(),

                application.getOrderId(),
                order.title(),
                order.status().name(),

                hunter.id(),
                hunter.callsign(),

                application.getMessage(),
                application.getProposedReward(),
                application.getStatus(),

                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }

    private ApplicationResponse toApplicationResponse(
            OrderApplication application,
            Map<UUID, HunterProfileRef> huntersById,
            Map<UUID, OrderRef> ordersById
    ) {
        HunterProfileRef hunter = huntersById.get(application.getHunterId());
        OrderRef order = ordersById.get(application.getOrderId());

        if (hunter == null) {
            throw new ResourceNotFoundException("Hunter profile not found: " + application.getHunterId());
        }

        if (order == null) {
            throw new ResourceNotFoundException("Order not found: " + application.getOrderId());
        }

        return new ApplicationResponse(
                application.getId(),
                application.getOrderId(),
                order.title(),
                order.status().name(),
                hunter.id(),
                hunter.callsign(),
                application.getMessage(),
                application.getProposedReward(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }

    private List<ApplicationResponse> toApplicationResponses(List<OrderApplication> applications) {
        if (applications.isEmpty()) {
            return List.of();
        }

        Map<UUID, HunterProfileRef> huntersById = profileLookup.getHunterProfilesByIds(
                applications.stream()
                        .map(OrderApplication::getHunterId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );

        Map<UUID, OrderRef> ordersById = orderLookup.getOrdersByIds(
                applications.stream()
                        .map(OrderApplication::getOrderId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );

        return applications.stream()
                .map(application -> toApplicationResponse(application, huntersById, ordersById))
                .toList();
    }
}
