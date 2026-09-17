package com.reorderkit.repository;

import com.reorderkit.entity.ProductVariant;
import com.reorderkit.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    Optional<ProductVariant> findByIdAndStore_ShopDomain(Long id, String shopDomain);
    Optional<ProductVariant> findByShopifyVariantId(String shopifyVariantId);
    List<ProductVariant> findAllByStore(Store store);
    Page<ProductVariant> findAllByStore(Store store, Pageable pageable);
}
