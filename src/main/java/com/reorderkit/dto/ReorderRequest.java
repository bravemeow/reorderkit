package com.reorderkit.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReorderRequest {
    @NotNull
    @Min(0)
    private Integer inventory;

    @NotNull
    @Positive
    private Double averageDailySales30d;

    @NotNull
    @Min(0)
    private Integer leadTimeDays;

    @NotNull
    @Positive
    private Integer reorderCycleDays;

    @NotNull
    @Min(0)
    private Integer bufferDays;

    public ReorderRequest() {
    }

    public Integer getInventory() { return inventory; }
    public Double getAverageDailySales30d() { return averageDailySales30d; }
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public Integer getReorderCycleDays() { return reorderCycleDays; }
    public Integer getBufferDays() { return bufferDays; }

    public void setInventory(Integer inventory) { this.inventory = inventory; }
    public void setAverageDailySales30d(Double value) { this.averageDailySales30d = value; }
    public void setLeadTimeDays(Integer value) { this.leadTimeDays = value; }
    public void setReorderCycleDays(Integer value) { this.reorderCycleDays = value; }
    public void setBufferDays(Integer value) { this.bufferDays = value; }
}
