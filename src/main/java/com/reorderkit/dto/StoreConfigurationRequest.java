package com.reorderkit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class StoreConfigurationRequest {
    @NotNull
    @PositiveOrZero
    private Integer leadTimeDays;

    @NotNull
    @PositiveOrZero
    private Integer bufferDays;

    @NotNull
    @Positive
    private Integer orderCoverageDays;

    public StoreConfigurationRequest() {
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public Integer getBufferDays() {
        return bufferDays;
    }

    public void setBufferDays(Integer bufferDays) {
        this.bufferDays = bufferDays;
    }

    public Integer getOrderCoverageDays() {
        return orderCoverageDays;
    }

    public void setOrderCoverageDays(Integer orderCoverageDays) {
        this.orderCoverageDays = orderCoverageDays;
    }
}
