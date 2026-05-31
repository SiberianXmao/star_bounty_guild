package com.stud.backend.applications.web;


import com.stud.backend.applications.service.OrderApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stud.backend.applications.web.dto.OrderApplicationDtos.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderApplicationController {

    private final OrderApplicationService orderApplicationService;

    @PostMapping("/orders/{orderId}/applications")
    public ApplicationResponse applyToOrder(
            Authentication authentication,
            @PathVariable UUID orderId,
            @Valid @RequestBody ApplicationCreateRequest request
    ) {
        return orderApplicationService.applyToOrder(authentication.getName(), orderId, request);
    }

    @GetMapping("/applications/my/hunter")
    public List<ApplicationResponse> getMyHunterApplications(Authentication authentication) {
        return orderApplicationService.getMyHunterApplications(authentication.getName());
    }

    @PostMapping("/applications/{applicationId}/withdraw")
    public ApplicationResponse withdrawMyApplication(
            Authentication authentication,
            @PathVariable UUID applicationId
    ) {
        return orderApplicationService.withdrawMyApplication(authentication.getName(), applicationId);
    }

    @GetMapping("/orders/my/client/{orderId}/applications")
    public List<ApplicationResponse> getApplicationsForMyOrder(
            Authentication authentication,
            @PathVariable UUID orderId
    ) {
        return orderApplicationService.getApplicationsForMyOrder(authentication.getName(), orderId);
    }

    @PostMapping("/applications/{applicationId}/accept")
    public ApplicationResponse acceptApplication(
            Authentication authentication,
            @PathVariable UUID applicationId
    ) {
        return orderApplicationService.acceptApplication(authentication.getName(), applicationId);
    }

    @PostMapping("/applications/{applicationId}/reject")
    public ApplicationResponse rejectApplication(
            Authentication authentication,
            @PathVariable UUID applicationId
    ) {
        return orderApplicationService.rejectApplication(authentication.getName(), applicationId);
    }
}