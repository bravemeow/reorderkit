package com.reorderkit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "shopify_product_id", nullable = false)
    private String shopifyProductId;

    @Column(name = "shopify_variant_id", nullable = false, unique = true)
    private String shopifyVariantId;

    @Column(name = "product_title", nullable = false)
    private String productTitle;

    @Column(name = "variant_title")
    private String variantTitle;

    @Column(name = "sku")
    private String sku;

    @Column(name = "inventory_quantity", nullable = false)
    private int inventoryQuantity;

    @Column(name = "on_order_quantity", nullable = false)
    private int onOrderQuantity;

    @Column(name = "average_daily_sales_30d", nullable = false, precision = 12, scale = 4)
    private BigDecimal averageDailySales30d;

    @Column(name = "lead_time_days_override")
    private Integer leadTimeDaysOverride;

    @Column(name = "buffer_days_override")
    private Integer bufferDaysOverride;

    @Column(name = "order_coverage_days_override")
    private Integer orderCoverageDaysOverride;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProductVariant() {
    }

    public ProductVariant(
            Store store,
            String shopifyProductId,
            String shopifyVariantId,
            String productTitle,
            String variantTitle,
            String sku,
            int inventoryQuantity,
            BigDecimal averageDailySales30d
    ) {
        this.store = store;
        this.shopifyProductId = shopifyProductId;
        this.shopifyVariantId = shopifyVariantId;
        this.productTitle = productTitle;
        this.variantTitle = variantTitle;
        this.sku = sku;
        this.inventoryQuantity = inventoryQuantity;
        this.onOrderQuantity = 0;
        this.averageDailySales30d = averageDailySales30d;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Store getStore() {
        return store;
    }

    public String getShopifyProductId() {
        return shopifyProductId;
    }

    public String getShopifyVariantId() {
        return shopifyVariantId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getVariantTitle() {
        return variantTitle;
    }

    public String getSku() {
        return sku;
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

    public Integer getLeadTimeDaysOverride() {
        return leadTimeDaysOverride;
    }

    public Integer getBufferDaysOverride() {
        return bufferDaysOverride;
    }

    public Integer getOrderCoverageDaysOverride() {
        return orderCoverageDaysOverride;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
