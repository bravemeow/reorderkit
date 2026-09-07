package com.reorderkit.dto;

public class ReorderResponse {
    private int estimateStockOutDays;
    private int reorderThreshold;
    private boolean shouldReorder;
    private int recommendedQuantity;

    public ReorderResponse(int estimateStockOutDays, int reorderThreshold, boolean shouldReorder, int recommendedQuantity) {
        this.estimateStockOutDays = estimateStockOutDays;
        this.reorderThreshold = reorderThreshold;
        this.shouldReorder = shouldReorder;
        this.recommendedQuantity = recommendedQuantity;
    }

    public int getEstimateStockOutDays() {
        return estimateStockOutDays;
    }
    public int getReorderThreshold() {
        return reorderThreshold;
    }
    public boolean isShouldReorder() {
        return shouldReorder;
    }
    public int getRecommendedQuantity() {
        return recommendedQuantity;
    }

    public void setEstimateStockOutDays(int estimateStockOutDays) {
        this.estimateStockOutDays = estimateStockOutDays;
    }
    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }
    public void setShouldReorder(boolean shouldReorder) {
        this.shouldReorder = shouldReorder;
    }
    public void setRecommendedQuantity(int recommendedQuantity) {
        this.recommendedQuantity = recommendedQuantity;
    }
}
