package com.stud.backend.orders.api;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface OrderLookup {

    OrderRef getOrder(UUID orderId);

    Map<UUID, OrderRef> getOrdersByIds(Collection<UUID> orderIds);
}
