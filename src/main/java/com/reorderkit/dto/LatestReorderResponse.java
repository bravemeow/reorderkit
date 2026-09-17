package com.reorderkit.dto;

import java.time.Instant;

public record LatestReorderResponse(
        int reorderPoint,
        int targetInventory,
        boolean shouldReorder,
        int recommendedQuantity,
        Instant calculatedAt
) {
}
