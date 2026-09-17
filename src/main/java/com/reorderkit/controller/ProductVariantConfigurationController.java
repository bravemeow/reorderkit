package com.reorderkit.controller;

import com.reorderkit.dto.ProductVariantConfigurationRequest;
import com.reorderkit.dto.ProductVariantConfigurationResponse;
import com.reorderkit.service.ProductVariantConfigurationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductVariantConfigurationController {
    private final ProductVariantConfigurationService service;

    public ProductVariantConfigurationController(ProductVariantConfigurationService service) {
        this.service = service;
    }

    @GetMapping("/stores/{shopDomain}/variants/{variantId}/configuration")
    public ProductVariantConfigurationResponse getConfiguration(
            @PathVariable String shopDomain, @PathVariable Long variantId) {
        return service.getConfiguration(shopDomain, variantId);
    }

    @PutMapping("/stores/{shopDomain}/variants/{variantId}/configuration")
    public ProductVariantConfigurationResponse updateConfiguration(
            @PathVariable String shopDomain, @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantConfigurationRequest request) {
        return service.updateConfiguration(shopDomain, variantId, request);
    }
}
