package com.reorderkit.service;

import com.reorderkit.dto.ProductVariantConfigurationRequest;
import com.reorderkit.dto.ProductVariantConfigurationResponse;
import com.reorderkit.entity.ProductVariant;
import com.reorderkit.exception.ProductVariantNotFoundException;
import com.reorderkit.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductVariantConfigurationService {
    private final ProductVariantRepository repository;

    public ProductVariantConfigurationService(ProductVariantRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ProductVariantConfigurationResponse getConfiguration(String shopDomain, Long variantId) {
        ProductVariant variant = repository.findByIdAndStore_ShopDomain(variantId, shopDomain)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId));
        return new ProductVariantConfigurationResponse(variant.getId(), variant.getLeadTimeDaysOverride(),
                variant.getBufferDaysOverride(), variant.getOrderCoverageDaysOverride());
    }

    @Transactional
    public ProductVariantConfigurationResponse updateConfiguration(
            String shopDomain, Long variantId, ProductVariantConfigurationRequest request) {
        ProductVariant variant = repository.findByIdAndStore_ShopDomain(variantId, shopDomain)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId));
        variant.configureReorderOverrides(request.leadTimeDays(), request.bufferDays(), request.orderCoverageDays());
        return new ProductVariantConfigurationResponse(variant.getId(), variant.getLeadTimeDaysOverride(),
                variant.getBufferDaysOverride(), variant.getOrderCoverageDaysOverride());
    }
}
