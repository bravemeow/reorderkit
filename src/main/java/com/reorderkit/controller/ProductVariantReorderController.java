package com.reorderkit.controller;

import com.reorderkit.dto.ReorderResponse;
import com.reorderkit.dto.LatestReorderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import com.reorderkit.service.ProductVariantReorderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductVariantReorderController {
    private final ProductVariantReorderService service;

    public ProductVariantReorderController(ProductVariantReorderService service) {
        this.service = service;
    }

    @PostMapping("/stores/{shopDomain}/variants/{variantId}/reorder/check")
    public ReorderResponse check(@PathVariable String shopDomain, @PathVariable Long variantId) {
        return service.check(shopDomain, variantId);
    }

    @GetMapping("/stores/{shopDomain}/variants/{variantId}/reorder/latest")
    public ResponseEntity<LatestReorderResponse> latest(@PathVariable String shopDomain,
                                                      @PathVariable Long variantId) {
        return service.findLatest(shopDomain, variantId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
