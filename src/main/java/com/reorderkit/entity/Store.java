package com.reorderkit.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shopify_shop_id", nullable = false, unique = true)
    private String shopifyShopId;

    @Column(name = "shop_domain", nullable = false, unique = true)
    private String shopDomain;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "access_token", nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "merchant_email", nullable = false, length = 320)
    private String merchantEmail;

    @Column(name = "notification_email_override", length = 320)
    private String notificationEmailOverride;

    @Column(name = "default_lead_time_days")
    private Integer defaultLeadTimeDays;

    @Column(name = "default_buffer_days")
    private Integer defaultBufferDays;

    @Column(name = "default_order_coverage_days")
    private Integer defaultOrderCoverageDays;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Store() {
    }

    public Store(
            String shopifyShopId,
            String shopDomain,
            String storeName,
            String accessToken,
            String merchantEmail
    ) {
        this.shopifyShopId = shopifyShopId;
        this.shopDomain = shopDomain;
        this.storeName = storeName;
        this.accessToken = accessToken;
        this.merchantEmail = merchantEmail;
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

    public String getShopifyShopId() {
        return shopifyShopId;
    }

    public String getShopDomain() {
        return shopDomain;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getMerchantEmail() {
        return merchantEmail;
    }

    public String getNotificationEmailOverride() {
        return notificationEmailOverride;
    }

    public Integer getDefaultLeadTimeDays() {
        return defaultLeadTimeDays;
    }

    public Integer getDefaultBufferDays() {
        return defaultBufferDays;
    }

    public Integer getDefaultOrderCoverageDays() {
        return defaultOrderCoverageDays;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
