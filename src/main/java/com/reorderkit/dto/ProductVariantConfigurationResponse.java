package com.reorderkit.dto;

public record ProductVariantConfigurationResponse(
        Long variantId,
        Integer leadTimeDays,
        Integer bufferDays,
        Integer orderCoverageDays
) {
}
