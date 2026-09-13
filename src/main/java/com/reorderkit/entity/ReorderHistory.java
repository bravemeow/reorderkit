package com.reorderkit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "reorder_history")
public class ReorderHistory {
    public enum CalculationTrigger {
        MANUAL_REFRESH,
        SCHEDULED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @Column(name = "inventory_quantity", nullable = false)
    private int inventoryQuantity;

    @Column(name = "on_order_quantity", nullable = false)
    private int onOrderQuantity;

    @Column(name = "average_daily_sales_30d", nullable = false, precision = 12, scale = 4)
    private BigDecimal averageDailySales30d;

    @Column(name = "lead_time_days", nullable = false)
    private int leadTimeDays;

    @Column(name = "buffer_days", nullable = false)
    private int bufferDays;

    @Column(name = "order_coverage_days", nullable = false)
    private int orderCoverageDays;

    @Column(name = "reorder_point", nullable = false)
    private int reorderPoint;

    @Column(name = "target_inventory", nullable = false)
    private int targetInventory;

    @Column(name = "should_reorder", nullable = false)
    private boolean shouldReorder;

    @Column(name = "recommended_quantity", nullable = false)
    private int recommendedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_trigger", nullable = false, length = 32)
    private CalculationTrigger calculationTrigger;

    @Column(name = "calculated_at", nullable = false, updatable = false)
    private Instant calculatedAt;

    protected ReorderHistory() {
    }

    public ReorderHistory(
            ProductVariant productVariant,
            int inventoryQuantity,
            int onOrderQuantity,
            BigDecimal averageDailySales30d,
            int leadTimeDays,
            int bufferDays,
            int orderCoverageDays,
            int reorderPoint,
            int targetInventory,
            boolean shouldReorder,
            int recommendedQuantity,
            CalculationTrigger calculationTrigger
    ) {
        this.productVariant = productVariant;
        this.inventoryQuantity = inventoryQuantity;
        this.onOrderQuantity = onOrderQuantity;
        this.averageDailySales30d = averageDailySales30d;
        this.leadTimeDays = leadTimeDays;
        this.bufferDays = bufferDays;
        this.orderCoverageDays = orderCoverageDays;
        this.reorderPoint = reorderPoint;
        this.targetInventory = targetInventory;
        this.shouldReorder = shouldReorder;
        this.recommendedQuantity = recommendedQuantity;
        this.calculationTrigger = calculationTrigger;
    }

    @PrePersist
    private void onCreate() {
        calculatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public int getInventoryQuantity() {
        return inventoryQuantity;
    }

    public int getOnOrderQuantity() {
        return onOrderQuantity;
    }

    public BigDecimal getAverageDailySales30d() {
        return averageDailySales30d;
    }

    public int getLeadTimeDays() {
        return leadTimeDays;
    }

    public int getBufferDays() {
        return bufferDays;
    }

    public int getOrderCoverageDays() {
        return orderCoverageDays;
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

    public CalculationTrigger getCalculationTrigger() {
        return calculationTrigger;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }
}
