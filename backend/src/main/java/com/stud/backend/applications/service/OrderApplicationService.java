package com.stud.backend.applications.service;


import com.stud.backend.applications.domain.OrderApplication;
import com.stud.backend.applications.domain.enums.ApplicationStatus;
import com.stud.backend.applications.repository.OrderApplicationRepository;
import com.stud.backend.common.exception.BadRequestException;
import com.stud.backend.common.exception.DuplicateResourceException;
import com.stud.backend.common.exception.ResourceNotFoundException;
import com.stud.backend.orders.domain.BountyOrder;
import com.stud.backend.orders.domain.enums.AcceptanceMode;
import com.stud.backend.orders.domain.enums.OrderStatus;
import com.stud.backend.orders.repository.BountyOrderRepository;
import com.stud.backend.profiles.domain.ClientProfile;
import com.stud.backend.profiles.domain.HunterProfile;
import com.stud.backend.profiles.repository.ClientProfileRepository;
import com.stud.backend.profiles.repository.HunterProfileRepository;
import com.stud.backend.users.domain.User;
import com.stud.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stud.backend.applications.web.dto.OrderApplicationDtos.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderApplicationService {

    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final HunterProfileRepository hunterProfileRepository;
    private final BountyOrderRepository bountyOrderRepository;
    private final OrderApplicationRepository orderApplicationRepository;

    @Transactional
    public ApplicationResponse applyToOrder(String email, UUID orderId, ApplicationCreateRequest request) {
        HunterProfile hunter = findCurrentHunterProfile(email);
        BountyOrder order = findOrder(orderId);

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new BadRequestException("Only OPEN orders can receive applications");
        }

        if (order.getAcceptanceMode() != AcceptanceMode.APPLICATIONS) {
            throw new BadRequestException("This order does not accept applications");
        }

        if (order.getClient().getUser().getId().equals(hunter.getUser().getId())) {
            throw new BadRequestException("You cannot apply to your own order");
        }

        if (orderApplicationRepository.existsByOrder_IdAndHunter_Id(order.getId(), hunter.getId())) {
            throw new DuplicateResourceException("You have already applied to this order");
        }

        OrderApplication application = new OrderApplication();
        application.setOrder(order);
        application.setHunter(hunter);
        application.setMessage(request.message());
        application.setProposedReward(request.proposedReward());
        application.setStatus(ApplicationStatus.PENDING);

        return toApplicationResponse(orderApplicationRepository.save(application));
    }

    public List<ApplicationResponse> getMyHunterApplications(String email) {
        HunterProfile hunter = findCurrentHunterProfile(email);

        return orderApplicationRepository.findAllByHunter_IdOrderByCreatedAtDesc(hunter.getId())
                .stream()
                .map(this::toApplicationResponse)
                .toList();
    }

    public List<ApplicationResponse> getApplicationsForMyOrder(String email, UUID orderId) {
        ClientProfile client = findCurrentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        ensureOrderOwner(order, client);

        return orderApplicationRepository.findAllByOrder_Id(order.getId())
                .stream()
                .map(this::toApplicationResponse)
                .toList();
    }

    @Transactional
    public ApplicationResponse withdrawMyApplication(String email, UUID applicationId) {
        HunterProfile hunter = findCurrentHunterProfile(email);
        OrderApplication application = findApplication(applicationId);

        if (!application.getHunter().getId().equals(hunter.getId())) {
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
        ClientProfile client = findCurrentClientProfile(email);
        OrderApplication application = findApplication(applicationId);
        BountyOrder order = application.getOrder();

        ensureOrderOwner(order, client);

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new BadRequestException("Only OPEN orders can accept applications");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only PENDING applications can be accepted");
        }

        application.setStatus(ApplicationStatus.ACCEPTED);

        List<OrderApplication> pendingApplications =
                orderApplicationRepository.findAllByOrder_IdAndStatus(order.getId(), ApplicationStatus.PENDING);

        for (OrderApplication otherApplication : pendingApplications) {
            if (!otherApplication.getId().equals(application.getId())) {
                otherApplication.setStatus(ApplicationStatus.REJECTED);
            }
        }

        order.setAssignedHunter(application.getHunter());
        order.setStatus(OrderStatus.ASSIGNED);

        bountyOrderRepository.save(order);
        orderApplicationRepository.saveAll(pendingApplications);

        return toApplicationResponse(orderApplicationRepository.save(application));
    }

    @Transactional
    public ApplicationResponse rejectApplication(String email, UUID applicationId) {
        ClientProfile client = findCurrentClientProfile(email);
        OrderApplication application = findApplication(applicationId);
        BountyOrder order = application.getOrder();

        ensureOrderOwner(order, client);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only PENDING applications can be rejected");
        }

        application.setStatus(ApplicationStatus.REJECTED);

        return toApplicationResponse(orderApplicationRepository.save(application));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private ClientProfile findCurrentClientProfile(String email) {
        User user = findUserByEmail(email);

        return clientProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found for current user"));
    }

    private HunterProfile findCurrentHunterProfile(String email) {
        User user = findUserByEmail(email);

        return hunterProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found for current user"));
    }

    private BountyOrder findOrder(UUID orderId) {
        return bountyOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    private OrderApplication findApplication(UUID applicationId) {
        return orderApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));
    }

    private void ensureOrderOwner(BountyOrder order, ClientProfile client) {
        if (!order.getClient().getId().equals(client.getId())) {
            throw new ResourceNotFoundException("Order not found: " + order.getId());
        }
    }

    private ApplicationResponse toApplicationResponse(OrderApplication application) {
        return new ApplicationResponse(
                application.getId(),

                application.getOrder().getId(),
                application.getOrder().getTitle(),
                application.getOrder().getStatus(),

                application.getHunter().getId(),
                application.getHunter().getCallsign(),

                application.getMessage(),
                application.getProposedReward(),
                application.getStatus(),

                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}
