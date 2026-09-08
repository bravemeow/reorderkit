package com.reorderkit.dto;

public class ReorderResponse {
    private int reorderPoint;
    private int targetInventory;
    private boolean shouldReorder;
    private int recommendedQuantity;
    public ReorderResponse(int reorderPoint, int targetInventory, boolean shouldReorder, int recommendedQuantity) {
        this.reorderPoint = reorderPoint;
        this.targetInventory = targetInventory;
        this.shouldReorder = shouldReorder;
        this.recommendedQuantity = recommendedQuantity;
    }

    public int getReorderPoint() {
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

    public void setReorderPoint(int reorderPoint) {
        this.reorderPoint = reorderPoint;
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
