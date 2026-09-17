package com.reorderkit.dto;

public class StoreConfigurationResponse {
    private final String shopDomain;
    private final Integer leadTimeDays;
    private final Integer bufferDays;
    private final Integer orderCoverageDays;
    private final boolean configured;

    public StoreConfigurationResponse(
            String shopDomain,
            Integer leadTimeDays,
            Integer bufferDays,
            Integer orderCoverageDays,
            boolean configured
    ) {
        this.shopDomain = shopDomain;
        this.leadTimeDays = leadTimeDays;
        this.bufferDays = bufferDays;
        this.orderCoverageDays = orderCoverageDays;
        this.configured = configured;
    }

    public String getShopDomain() {
        return shopDomain;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public Integer getBufferDays() {
        return bufferDays;
    }

    public Integer getOrderCoverageDays() {
        return orderCoverageDays;
    }

    public boolean isConfigured() {
        return configured;
    }
}
