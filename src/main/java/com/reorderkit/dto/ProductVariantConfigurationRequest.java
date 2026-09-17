package com.reorderkit.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductVariantConfigurationRequest(
        @PositiveOrZero Integer leadTimeDays,
        @PositiveOrZero Integer bufferDays,
        @Positive Integer orderCoverageDays
) {
}
