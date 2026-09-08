package com.reorderkit.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReorderRequest {
    @NotNull
    @Min(0)
    private Integer inventory;

    @Min(0)
    private Integer onOrderQuantity;

    @NotNull
    @Positive
    private Double averageDailySales;

    @NotNull
    @Min(0)
    private Integer leadTimeDays;

    @NotNull
    @Min(0)
    private Integer bufferDays;

    @NotNull
    @Min(0)
    private Integer orderCoverageDays;

    public ReorderRequest() {
    }

    public Integer getInventory() { return inventory; }
    public Integer getOnOrderQuantity() {
        return onOrderQuantity;
    }
    public Double getAverageDailySales() { return averageDailySales; }
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public Integer getBufferDays() { return bufferDays; }
    public Integer getOrderCoverageDays() { return orderCoverageDays; }

    public void setInventory(Integer inventory) { this.inventory = inventory; }
    public void setOnOrderQuantity(Integer onOrderQuantity) {
        this.onOrderQuantity = onOrderQuantity;
    }
    public void setAverageDailySales30d(Double value) { this.averageDailySales = value; }
    public void setLeadTimeDays(Integer value) { this.leadTimeDays = value; }
    public void setBufferDays(Integer value) { this.bufferDays = value; }
    public void setOrderCoverageDays(Integer value) { this.orderCoverageDays = value; }
}
