package com.stud.orders.orders.web;


import com.stud.orders.orders.domain.enums.AcceptanceMode;
import com.stud.orders.orders.domain.enums.RiskLevel;
import com.stud.orders.orders.domain.enums.UrgencyLevel;
import com.stud.orders.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.stud.orders.orders.web.dto.OrderDtos.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createDraft(
            Authentication authentication,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        return orderService.createDraft(authentication.getName(), request);
    }

    @PatchMapping("/{orderId}")
    public OrderResponse updateDraft(
            Authentication authentication,
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderUpdateDraftRequest request
    ) {
        return orderService.updateDraft(authentication.getName(), orderId, request);
    }

    @PostMapping("/{orderId}/publish")
    public OrderResponse publish(
            Authentication authentication,
            @PathVariable UUID orderId
    ) {
        return orderService.publish(authentication.getName(), orderId);
    }

    @PostMapping("/{orderId}/cancel")
    public OrderResponse cancel(
            Authentication authentication,
            @PathVariable UUID orderId
    ) {
        return orderService.cancel(authentication.getName(), orderId);
    }

    @GetMapping
    public PageResponse<OrderResponse> getPublicOrders(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID planetId,
            @RequestParam(required = false) UUID sectorId,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) UrgencyLevel urgencyLevel,
            @RequestParam(required = false) AcceptanceMode acceptanceMode,
            @RequestParam(required = false) BigDecimal rewardMin,
            @RequestParam(required = false) BigDecimal rewardMax,
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        return orderService.getPublicOrders(
                categoryId,
                planetId,
                sectorId,
                riskLevel,
                urgencyLevel,
                acceptanceMode,
                rewardMin,
                rewardMax,
                q,
                pageable
        );
    }

    @GetMapping("/{orderId}")
    public OrderResponse getPublicOrder(@PathVariable UUID orderId) {
        return orderService.getPublicOrder(orderId);
    }

    @GetMapping("/my/client")
    public PageResponse<OrderResponse> getMyClientOrders(
            Authentication authentication,
            Pageable pageable
    ) {
        return orderService.getMyClientOrders(authentication.getName(), pageable);
    }

    @GetMapping("/my/client/{orderId}")
    public OrderResponse getMyClientOrder(
            Authentication authentication,
            @PathVariable UUID orderId
    ) {
        return orderService.getMyClientOrder(authentication.getName(), orderId);
    }

    @GetMapping("/my/hunter")
    public PageResponse<OrderResponse> getMyHunterOrders(
            Authentication authentication,
            Pageable pageable
    ) {
        return orderService.getMyHunterOrders(authentication.getName(), pageable);
    }

    @GetMapping("/my/hunter/{orderId}")
    public OrderResponse getMyHunterOrder(
            Authentication authentication,
            @PathVariable UUID orderId
    ) {
        return orderService.getMyHunterOrder(authentication.getName(), orderId);
    }

    @PostMapping("/{orderId}/start")
    public OrderResponse startOrder(
            Authentication authentication,
            @PathVariable UUID orderId
    ) {
        return orderService.startOrder(authentication.getName(), orderId);
    }

    @PostMapping("/{orderId}/submit")
    public OrderResponse submitOrder(
            Authentication authentication,
            @PathVariable UUID orderId
    ) {
        return orderService.submitOrder(authentication.getName(), orderId);
    }

    @PostMapping("/{orderId}/complete")
    public OrderResponse completeOrder(
            Authentication authentication,
            @PathVariable UUID orderId
    ) {
        return orderService.completeOrder(authentication.getName(), orderId);
    }
}
