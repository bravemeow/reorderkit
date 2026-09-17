package com.reorderkit.service;

import com.reorderkit.dto.ReorderRequest;
import com.reorderkit.dto.LatestReorderResponse;
import java.util.Optional;
import com.reorderkit.dto.ReorderResponse;
import com.reorderkit.entity.ProductVariant;
import com.reorderkit.entity.ReorderHistory;
import com.reorderkit.entity.Store;
import com.reorderkit.exception.ProductVariantNotFoundException;
import com.reorderkit.exception.ReorderNotReadyException;
import com.reorderkit.repository.ProductVariantRepository;
import com.reorderkit.repository.ReorderHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductVariantReorderService {
    private final ProductVariantRepository variantRepository;
    private final ReorderHistoryRepository historyRepository;
    private final ReorderService reorderService;

    public ProductVariantReorderService(ProductVariantRepository variantRepository,
                                       ReorderHistoryRepository historyRepository,
                                       ReorderService reorderService) {
        this.variantRepository = variantRepository;
        this.historyRepository = historyRepository;
        this.reorderService = reorderService;
    }

    @Transactional
    public ReorderResponse check(String shopDomain, Long variantId) {
        ProductVariant variant = variantRepository.findByIdAndStore_ShopDomain(variantId, shopDomain)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId));
        Store store = variant.getStore();
        if (!store.isConfigured()) {
            throw new ReorderNotReadyException("Complete store configuration before calculating reorder recommendations");
        }
        // The existing calculator requires a positive sales rate.
        if (!Double.isFinite(variant.getAverageDailySales30d()) || variant.getAverageDailySales30d() <= 0) {
            throw new ReorderNotReadyException("A positive average daily sales value is required for calculation");
        }

        int leadTimeDays = resolve(variant.getLeadTimeDaysOverride(), store.getDefaultLeadTimeDays());
        int bufferDays = resolve(variant.getBufferDaysOverride(), store.getDefaultBufferDays());
        int orderCoverageDays = resolve(variant.getOrderCoverageDaysOverride(), store.getDefaultOrderCoverageDays());

        // Adapt persisted values to the existing calculator's input contract.
        ReorderRequest input = new ReorderRequest();
        input.setInventory(variant.getInventoryQuantity());
        input.setOnOrderQuantity(variant.getOnOrderQuantity());
        input.setAverageDailySales(variant.getAverageDailySales30d());
        input.setLeadTimeDays(leadTimeDays);
        input.setBufferDays(bufferDays);
        input.setOrderCoverageDays(orderCoverageDays);
        ReorderResponse result = reorderService.check(input);

        historyRepository.save(new ReorderHistory(
                variant, variant.getInventoryQuantity(), variant.getOnOrderQuantity(),
                variant.getAverageDailySales30d(), leadTimeDays, bufferDays, orderCoverageDays,
                result.getReorderPoint(), result.getTargetInventory(), result.isShouldReorder(),
                result.getRecommendedQuantity(), ReorderHistory.CalculationTrigger.MANUAL_REFRESH
        ));
        return result;
    }

    @Transactional(readOnly = true)
    public Optional<LatestReorderResponse> findLatest(String shopDomain, Long variantId) {
        ProductVariant variant = variantRepository.findByIdAndStore_ShopDomain(variantId, shopDomain)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId));
        return historyRepository.findTopByProductVariantOrderByCalculatedAtDescIdDesc(variant)
                .map(history -> new LatestReorderResponse(
                        history.getReorderPoint(), history.getTargetInventory(),
                        history.isShouldReorder(), history.getRecommendedQuantity(),
                        history.getCalculatedAt()
                ));
    }

    private int resolve(Integer override, Integer defaultValue) {
        return override != null ? override : defaultValue;
    }
}
