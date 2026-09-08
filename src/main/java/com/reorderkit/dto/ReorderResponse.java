package com.reorderkit.dto;

public class ReorderResponse {
    private double reorderPoint;
    private int targetInventory;
    private boolean shouldReorder;
    private int recommendedQuantity;
    public ReorderResponse(double reorderPoint, int targetInventory, boolean shouldReorder, int recommendedQuantity) {
        this.reorderPoint = reorderPoint;
        this.targetInventory = targetInventory;
        this.shouldReorder = shouldReorder;
        this.recommendedQuantity = recommendedQuantity;
    }

    public double getReorderPoint() {
        return reorderPoint;
    }
    public int getTargetInventory() {
        return targetInventory;
    }
    public boolean isShouldReorder() {
        return shouldReorder;
    }
    public int getRecommendedQuantity() {
        return recommendedQuantity;
    }

    public void setReorderPoint(int estimateStockOutDays) {
        this.reorderPoint = estimateStockOutDays;
    }
    public void setTargetInventory(int reorderThreshold) {
        this.targetInventory = reorderThreshold;
    }
    public void setShouldReorder(boolean shouldReorder) {
        this.shouldReorder = shouldReorder;
    }
    public void setRecommendedQuantity(int recommendedQuantity) {
        this.recommendedQuantity = recommendedQuantity;
    }
}
