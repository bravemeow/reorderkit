package com.reorderkit.service;

import com.reorderkit.dto.ProductVariantPageResponse;
import com.reorderkit.dto.ProductVariantResponse;
import com.reorderkit.exception.StoreNotFoundException;
import com.reorderkit.repository.ProductVariantRepository;
import com.reorderkit.repository.StoreRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductVariantQueryService {
    private final StoreRepository stores;
    private final ProductVariantRepository variants;

    public ProductVariantQueryService(StoreRepository stores, ProductVariantRepository variants) {
        this.stores = stores;
        this.variants = variants;
    }

    @Transactional(readOnly = true)
    public ProductVariantPageResponse findAll(String shopDomain, int page, int size) {
        var store = stores.findByShopDomain(shopDomain)
                .orElseThrow(() -> new StoreNotFoundException(shopDomain));
        var results = variants.findAllByStore(store, PageRequest.of(page, size, Sort.by("id")));
        var items = results.getContent().stream().map(variant -> new ProductVariantResponse(
                variant.getId(), variant.getShopifyProductId(), variant.getShopifyVariantId(),
                variant.getProductTitle(), variant.getVariantTitle(), variant.getSku(),
                variant.getInventoryQuantity(), variant.getOnOrderQuantity(),
                variant.getAverageDailySales30d(), variant.getLastSyncedAt()
        )).toList();
        return new ProductVariantPageResponse(items, page, size, results.getTotalElements(), results.getTotalPages());
    }
}
