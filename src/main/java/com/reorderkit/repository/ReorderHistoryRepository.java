package com.reorderkit.repository;

import com.reorderkit.entity.ProductVariant;
import com.reorderkit.entity.ReorderHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReorderHistoryRepository extends JpaRepository<ReorderHistory, Long> {
    Optional<ReorderHistory> findTopByProductVariantOrderByCalculatedAtDescIdDesc(
            ProductVariant productVariant
    );

    Page<ReorderHistory> findAllByProductVariantOrderByCalculatedAtDescIdDesc(
            ProductVariant productVariant,
            Pageable pageable
    );
}
