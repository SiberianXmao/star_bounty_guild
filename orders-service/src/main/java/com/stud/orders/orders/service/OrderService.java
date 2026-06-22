package com.stud.orders.orders.service;

import com.stud.orders.orders.domain.enums.AcceptanceMode;
import com.stud.orders.orders.domain.enums.RiskLevel;
import com.stud.orders.orders.domain.enums.UrgencyLevel;
import com.stud.orders.orders.web.dto.OrderDtos.OrderCreateRequest;
import com.stud.orders.orders.web.dto.OrderDtos.OrderResponse;
import com.stud.orders.orders.web.dto.OrderDtos.OrderUpdateDraftRequest;
import com.stud.orders.orders.web.dto.OrderDtos.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderService {

    OrderResponse createDraft(String email, OrderCreateRequest request);

    OrderResponse updateDraft(String email, UUID orderId, OrderUpdateDraftRequest request);

    OrderResponse publish(String email, UUID orderId);

    OrderResponse cancel(String email, UUID orderId);

    PageResponse<OrderResponse> getMyHunterOrders(String email, Pageable pageable);

    OrderResponse getMyHunterOrder(String email, UUID orderId);

    OrderResponse startOrder(String email, UUID orderId);

    OrderResponse submitOrder(String email, UUID orderId);

    OrderResponse completeOrder(String email, UUID orderId);

    PageResponse<OrderResponse> getPublicOrders(
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
    );

    OrderResponse getPublicOrder(UUID orderId);

    PageResponse<OrderResponse> getMyClientOrders(String email, Pageable pageable);

    OrderResponse getMyClientOrder(String email, UUID orderId);
}
