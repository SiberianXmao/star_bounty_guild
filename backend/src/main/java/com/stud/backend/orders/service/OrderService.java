package com.stud.backend.orders.service;


import com.stud.backend.common.exception.BadRequestException;
import com.stud.backend.common.exception.ResourceNotFoundException;

import com.stud.backend.dictionary.api.DictionaryLookup;
import com.stud.backend.dictionary.api.CurrencyRef;
import com.stud.backend.dictionary.api.OrderCategoryRef;
import com.stud.backend.dictionary.api.PlanetRef;
import com.stud.backend.dictionary.api.SectorRef;

import com.stud.backend.orders.domain.BountyOrder;
import com.stud.backend.orders.domain.enums.AcceptanceMode;
import com.stud.backend.orders.domain.enums.OrderStatus;
import com.stud.backend.orders.domain.enums.OrderVisibility;
import com.stud.backend.orders.domain.enums.RiskLevel;
import com.stud.backend.orders.domain.enums.UrgencyLevel;
import com.stud.backend.orders.repository.BountyOrderRepository;
import com.stud.backend.orders.repository.BountyOrderSpecifications;
import com.stud.backend.orders.web.dto.OrderDtos;

import com.stud.backend.profiles.domain.ClientProfile;
import com.stud.backend.profiles.domain.HunterProfile;
import com.stud.backend.profiles.repository.ClientProfileRepository;
import com.stud.backend.profiles.repository.HunterProfileRepository;

import com.stud.backend.users.domain.User;
import com.stud.backend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stud.backend.orders.web.dto.OrderDtos.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;

    // ------------------- замена
    private final DictionaryLookup dictionaryLookup;

    //----------------

    private final HunterProfileRepository hunterProfileRepository;

    private final BountyOrderRepository bountyOrderRepository;

    @Transactional
    public OrderResponse createDraft(String email, OrderDtos.OrderCreateRequest request) {
        ClientProfile clientProfile = findCurrentClientProfile(email);
        OrderCategoryRef category = findCategory(request.categoryId());
        CurrencyRef currency = findCurrency(request.rewardCurrencyCode());
        PlanetRef planet = request.planetId() == null ? null : findPlanet(request.planetId());
        SectorRef sector = request.sectorId() == null ? null : findSector(request.sectorId());

        BountyOrder order = new BountyOrder();
        order.setClient(clientProfile);
        order.setAssignedHunter(null);
        order.setTitle(request.title().trim());
        order.setDescription(request.description().trim());

        order.setCategoryId(category.id());
        //order.setCategory(findCategoryEntity(request.categoryId()));

        order.setRewardAmount(request.rewardAmount());


        order.setRewardCurrencyCode(currency.code());
        order.setPlanetId(planet == null ? null : planet.id());
        order.setSectorId(sector == null ? null : sector.id());
        //order.setRewardCurrency(findCurrencyEntity(request.rewardCurrencyCode()));
        //order.setPlanet(request.planetId() == null ? null : findPlanetEntity(request.planetId()));
        //order.setSector(request.sectorId() == null ? null : findSectorEntity(request.sectorId()));

        order.setRiskLevel(request.riskLevel());
        order.setUrgencyLevel(request.urgencyLevel());
        order.setStatus(OrderStatus.DRAFT);
        order.setVisibility(request.visibility() == null ? OrderVisibility.PUBLIC : request.visibility());
        order.setAcceptanceMode(request.acceptanceMode() == null ? AcceptanceMode.APPLICATIONS : request.acceptanceMode());
        order.setRequirements(request.requirements());
        order.setDeadline(request.deadline());

        return toOrderResponse(bountyOrderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateDraft(String email, UUID orderId, OrderUpdateDraftRequest request) {
        ClientProfile clientProfile = findCurrentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        ensureOwner(order, clientProfile);
        ensureDraft(order);

        if (request.title() != null) {
            order.setTitle(request.title().trim());
        }

        if (request.description() != null) {
            order.setDescription(request.description().trim());
        }

        if (request.categoryId() != null) {
            order.setCategoryId(findCategory(request.categoryId()).id());
        }

        if (request.rewardAmount() != null) {
            order.setRewardAmount(request.rewardAmount());
        }

        if (request.rewardCurrencyCode() != null) {
            order.setRewardCurrencyCode(findCurrency(request.rewardCurrencyCode()).code());
        }

        if (request.planetId() != null) {
            order.setPlanetId(findPlanet(request.planetId()).id());
        }

        if (request.sectorId() != null) {
            order.setSectorId(findSector(request.sectorId()).id());
        }

        if (request.riskLevel() != null) {
            order.setRiskLevel(request.riskLevel());
        }

        if (request.urgencyLevel() != null) {
            order.setUrgencyLevel(request.urgencyLevel());
        }

        if (request.visibility() != null) {
            order.setVisibility(request.visibility());
        }

        if (request.acceptanceMode() != null) {
            order.setAcceptanceMode(request.acceptanceMode());
        }

        if (request.requirements() != null) {
            order.setRequirements(request.requirements());
        }

        if (request.deadline() != null) {
            order.setDeadline(request.deadline());
        }

        return toOrderResponse(bountyOrderRepository.save(order));
    }

    @Transactional
    public OrderResponse publish(String email, UUID orderId) {
        ClientProfile clientProfile = findCurrentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        ensureOwner(order, clientProfile);
        ensureDraft(order);

        if (order.getVisibility() != OrderVisibility.PUBLIC) {
            throw new BadRequestException("Only PUBLIC orders can be published to the board in this module");
        }

        if (order.getAcceptanceMode() == AcceptanceMode.PERSONAL_OFFER) {
            throw new BadRequestException("PERSONAL_OFFER orders will be handled in offers module");
        }

        order.setStatus(OrderStatus.OPEN);
        order.setPublishedAt(Instant.now());

        return toOrderResponse(bountyOrderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancel(String email, UUID orderId) {
        ClientProfile clientProfile = findCurrentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        ensureOwner(order, clientProfile);

        if (order.getStatus() != OrderStatus.DRAFT && order.getStatus() != OrderStatus.OPEN) {
            throw new BadRequestException("Only DRAFT or OPEN orders can be cancelled now");
        }

        order.setStatus(OrderStatus.CANCELLED);

        return toOrderResponse(bountyOrderRepository.save(order));
    }

    public PageResponse<OrderResponse> getMyHunterOrders(String email, Pageable pageable) {
        HunterProfile hunterProfile = findCurrentHunterProfile(email);

        Page<OrderResponse> page = bountyOrderRepository.findAllByAssignedHunterId(hunterProfile.getId(), pageable)
                .map(this::toOrderResponse);

        return toPageResponse(page);
    }

    public OrderResponse getMyHunterOrder(String email, UUID orderId) {
        HunterProfile hunterProfile = findCurrentHunterProfile(email);
        BountyOrder order = findOrder(orderId);

        ensureAssignedHunter(order, hunterProfile);

        return toOrderResponse(order);
    }

    @Transactional
    public OrderResponse startOrder(String email, UUID orderId) {
        HunterProfile hunterProfile = findCurrentHunterProfile(email);
        BountyOrder order = findOrder(orderId);

        ensureAssignedHunter(order, hunterProfile);

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new BadRequestException("Only ASSIGNED orders can be started");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);

        return toOrderResponse(bountyOrderRepository.save(order));
    }

    @Transactional
    public OrderResponse submitOrder(String email, UUID orderId) {
        HunterProfile hunterProfile = findCurrentHunterProfile(email);
        BountyOrder order = findOrder(orderId);

        ensureAssignedHunter(order, hunterProfile);

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new BadRequestException("Only IN_PROGRESS orders can be submitted");
        }

        order.setStatus(OrderStatus.SUBMITTED);

        return toOrderResponse(bountyOrderRepository.save(order));
    }

    @Transactional
    public OrderResponse completeOrder(String email, UUID orderId) {
        ClientProfile clientProfile = findCurrentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        ensureOwner(order, clientProfile);

        if (order.getStatus() != OrderStatus.SUBMITTED) {
            throw new BadRequestException("Only SUBMITTED orders can be completed");
        }

        if (order.getAssignedHunter() == null) {
            throw new BadRequestException("Order has no assigned hunter");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(Instant.now());

        ClientProfile client = order.getClient();
        HunterProfile hunter = order.getAssignedHunter();

        client.setCompletedOrdersCount(client.getCompletedOrdersCount() + 1);
        hunter.setCompletedOrdersCount(hunter.getCompletedOrdersCount() + 1);

        return toOrderResponse(bountyOrderRepository.save(order));
    }

    private void ensureAssignedHunter(BountyOrder order, HunterProfile hunterProfile) {
        if (order.getAssignedHunter() == null || !order.getAssignedHunter().getId().equals(hunterProfile.getId())) {
            throw new ResourceNotFoundException("Order not found: " + order.getId());
        }
    }

    private HunterProfile findCurrentHunterProfile(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return hunterProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found for current user"));
    }

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
        Specification<BountyOrder> spec = Specification.<BountyOrder>unrestricted()
                .and(BountyOrderSpecifications.publicBoard())
                .and(BountyOrderSpecifications.categoryId(categoryId))
                .and(BountyOrderSpecifications.planetId(planetId))
                .and(BountyOrderSpecifications.sectorId(sectorId))
                .and(BountyOrderSpecifications.riskLevel(riskLevel))
                .and(BountyOrderSpecifications.urgencyLevel(urgencyLevel))
                .and(BountyOrderSpecifications.acceptanceMode(acceptanceMode))
                .and(BountyOrderSpecifications.rewardMin(rewardMin))
                .and(BountyOrderSpecifications.rewardMax(rewardMax))
                .and(BountyOrderSpecifications.search(q));

        Page<OrderResponse> page = bountyOrderRepository.findAll(spec, pageable)
                .map(this::toOrderResponse);

        return toPageResponse(page);
    }

    public OrderResponse getPublicOrder(UUID orderId) {
        BountyOrder order = findOrder(orderId);

        if (order.getVisibility() != OrderVisibility.PUBLIC || order.getStatus() != OrderStatus.OPEN) {
            throw new ResourceNotFoundException("Order not found: " + orderId);
        }

        return toOrderResponse(order);
    }

    public PageResponse<OrderResponse> getMyClientOrders(String email, Pageable pageable) {
        ClientProfile clientProfile = findCurrentClientProfile(email);

        Page<OrderResponse> page = bountyOrderRepository.findAllByClientId(clientProfile.getId(), pageable)
                .map(this::toOrderResponse);

        return toPageResponse(page);
    }

    public OrderResponse getMyClientOrder(String email, UUID orderId) {
        ClientProfile clientProfile = findCurrentClientProfile(email);
        BountyOrder order = findOrder(orderId);

        ensureOwner(order, clientProfile);

        return toOrderResponse(order);
    }

    private ClientProfile findCurrentClientProfile(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return clientProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found for current user"));
    }

    private BountyOrder findOrder(UUID orderId) {
        return bountyOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    // ------ entity пока оставлем чтобы все не сломать
    private OrderCategoryRef findCategory(UUID categoryId) {
        OrderCategoryRef category = dictionaryLookup.getOrderCategory(categoryId);

        if (!Boolean.TRUE.equals(category.active())){
            throw new BadRequestException("Category is not active " + categoryId);
        }
        return category;
    }

    private CurrencyRef findCurrency(String code) {

        CurrencyRef currency = dictionaryLookup.getCurrency(code);

        if (!Boolean.TRUE.equals(currency.active())){
            throw new BadRequestException("Currency is not active " + code);
        }
        return currency;
    }

    private PlanetRef findPlanet(UUID planetId) {
        return dictionaryLookup.getPlanet(planetId);

    }

    private SectorRef findSector(UUID sectorId) {
        return dictionaryLookup.getSector(sectorId);

    }

    //------

    private void ensureOwner(BountyOrder order, ClientProfile clientProfile) {
        if (!order.getClient().getId().equals(clientProfile.getId())) {
            throw new ResourceNotFoundException("Order not found: " + order.getId());
        }
    }

    private void ensureDraft(BountyOrder order) {
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT orders can be changed by this action");
        }
    }

    private OrderResponse toOrderResponse(BountyOrder order) {
        ClientProfile client = order.getClient();
        HunterProfile hunter = order.getAssignedHunter();

        OrderCategoryRef category = findCategory(order.getCategoryId());
        CurrencyRef currency = findCurrency(order.getRewardCurrencyCode());
        PlanetRef planet = order.getPlanetId() == null ? null : findPlanet(order.getPlanetId());
        SectorRef sector = order.getSectorId() == null ? null : findSector(order.getSectorId());
        //OrderCategory category = order.getCategory();
        //Currency currency = order.getRewardCurrency();
        //Planet planet = order.getPlanet();
        //Sector sector = order.getSector();

        return new OrderResponse(
                order.getId(),

                client.getId(),
                client.getName(),
                client.getAverageRating(),
                client.getReliabilityScore(),

                hunter == null ? null : hunter.getId(),
                hunter == null ? null : hunter.getCallsign(),

                order.getTitle(),
                order.getDescription(),

                category.id(),
                category.name(),

                order.getRewardAmount(),
                currency.code(),
                currency.name(),
                currency.symbol(),

                planet == null ? null : planet.id(),
                planet == null ? null : planet.name(),

                sector == null ? null : sector.id(),
                sector == null ? null : sector.name(),

                order.getRiskLevel(),
                order.getUrgencyLevel(),
                order.getStatus(),
                order.getVisibility(),
                order.getAcceptanceMode(),

                order.getRequirements(),
                order.getDeadline(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getPublishedAt(),
                order.getCompletedAt()
        );
    }

    private <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
