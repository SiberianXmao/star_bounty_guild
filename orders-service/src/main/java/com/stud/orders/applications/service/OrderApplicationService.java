package com.stud.orders.applications.service;

import com.stud.orders.applications.web.dto.OrderApplicationDtos.ApplicationCreateRequest;
import com.stud.orders.applications.web.dto.OrderApplicationDtos.ApplicationResponse;

import java.util.List;
import java.util.UUID;

public interface OrderApplicationService {

    ApplicationResponse applyToOrder(String email, UUID orderId, ApplicationCreateRequest request);

    List<ApplicationResponse> getMyHunterApplications(String email);

    List<ApplicationResponse> getApplicationsForMyOrder(String email, UUID orderId);

    ApplicationResponse withdrawMyApplication(String email, UUID applicationId);

    ApplicationResponse acceptApplication(String email, UUID applicationId);

    ApplicationResponse rejectApplication(String email, UUID applicationId);
}
