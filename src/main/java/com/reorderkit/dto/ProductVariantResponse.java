package com.reorderkit.dto;

import java.time.Instant;

public record ProductVariantResponse(
        Long variantId, String shopifyProductId, String shopifyVariantId,
        String productTitle, String variantTitle, String sku,
        int inventoryQuantity, int onOrderQuantity, double averageDailySales30d,
        Instant lastSyncedAt
) {
}
