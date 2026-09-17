package com.reorderkit;

import com.reorderkit.entity.ProductVariant;
import com.reorderkit.entity.ReorderHistory;
import com.reorderkit.entity.Store;
import com.reorderkit.exception.ProductVariantNotFoundException;
import com.reorderkit.exception.ReorderNotReadyException;
import com.reorderkit.repository.ProductVariantRepository;
import com.reorderkit.repository.ReorderHistoryRepository;
import com.reorderkit.repository.StoreRepository;
import com.reorderkit.service.ProductVariantReorderService;
import com.reorderkit.service.ReorderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ProductVariantReorderService.class, ReorderService.class})
class ProductVariantReorderServiceIntegrationTest {
    @Autowired private ProductVariantReorderService service;
    @Autowired private StoreRepository stores;
    @Autowired private ProductVariantRepository variants;
    @Autowired private ReorderHistoryRepository histories;
    @Autowired private TestEntityManager entityManager;

    @Test
    void calculatesFromStoredDefaultsAndPersistsInputAndResultSnapshot() {
        ProductVariant variant = createVariant(true, 10.0);
        Long id = variant.getId();
        entityManager.clear();

        var response = service.check("calculation.myshopify.com", id);
        entityManager.flush();
        entityManager.clear();
        ReorderHistory history = histories.findTopByProductVariantOrderByCalculatedAtDescIdDesc(
                variants.findById(id).orElseThrow()).orElseThrow();

        assertThat(response.getReorderPoint()).isEqualTo(250);
        assertThat(response.getTargetInventory()).isEqualTo(450);
        assertThat(response.isShouldReorder()).isTrue();
        assertThat(response.getRecommendedQuantity()).isEqualTo(350);
        assertThat(history.getInventoryQuantity()).isEqualTo(100);
        assertThat(history.getOnOrderQuantity()).isZero();
        assertThat(history.getAverageDailySales30d()).isEqualTo(10.0);
        assertThat(history.getLeadTimeDays()).isEqualTo(20);
        assertThat(history.getBufferDays()).isEqualTo(5);
        assertThat(history.getOrderCoverageDays()).isEqualTo(20);
        assertThat(history.getReorderPoint()).isEqualTo(250);
        assertThat(history.getTargetInventory()).isEqualTo(450);
        assertThat(history.isShouldReorder()).isTrue();
        assertThat(history.getRecommendedQuantity()).isEqualTo(350);
        assertThat(history.getCalculatedAt()).isNotNull();
        assertThat(history.getCalculationTrigger()).isEqualTo(ReorderHistory.CalculationTrigger.MANUAL_REFRESH);

        // Changing defaults later must not rewrite a historical input snapshot.
        Store store = stores.findByShopDomain("calculation.myshopify.com").orElseThrow();
        store.configureReorderSettings(30, 10, 7);
        entityManager.flush();
        entityManager.clear();
        assertThat(histories.findById(history.getId()).orElseThrow().getLeadTimeDays()).isEqualTo(20);
    }

    @Test
    void appliesPartialOverridesIncludingZeroAndUsesStoredOnOrderQuantity() {
        ProductVariant variant = createVariant(true, 10.0);
        // Set up persisted overrides before an editing API exists.
        entityManager.getEntityManager().createNativeQuery("""
                update product_variants set buffer_days_override = 0, on_order_quantity = 50
                where id = :id
                """).setParameter("id", variant.getId()).executeUpdate();
        entityManager.clear();

        var result = service.check("calculation.myshopify.com", variant.getId());
        entityManager.flush();
        entityManager.clear();
        var history = histories.findTopByProductVariantOrderByCalculatedAtDescIdDesc(
                variants.findById(variant.getId()).orElseThrow()).orElseThrow();
        assertThat(result.getReorderPoint()).isEqualTo(200);
        assertThat(result.getTargetInventory()).isEqualTo(400);
        assertThat(result.getRecommendedQuantity()).isEqualTo(250);
        assertThat(history.getBufferDays()).isZero();
        assertThat(history.getLeadTimeDays()).isEqualTo(20);
        assertThat(history.getOrderCoverageDays()).isEqualTo(20);
        assertThat(history.getOnOrderQuantity()).isEqualTo(50);
    }

    @Test
    void rejectsVariantFromAnotherStoreWithoutSavingHistory() {
        ProductVariant variant = createVariant(true, 10.0);
        long before = histories.count();
        assertThatThrownBy(() -> service.check("another.myshopify.com", variant.getId()))
                .isInstanceOf(ProductVariantNotFoundException.class);
        assertThat(histories.count()).isEqualTo(before);
    }

    @Test
    void rejectsIncompleteConfigurationWithoutSavingHistory() {
        ProductVariant variant = createVariant(false, 10.0);
        long before = histories.count();
        assertThatThrownBy(() -> service.check("calculation.myshopify.com", variant.getId()))
                .isInstanceOf(ReorderNotReadyException.class);
        assertThat(histories.count()).isEqualTo(before);
    }

    @Test
    void rejectsZeroSalesWithoutSavingHistory() {
        ProductVariant variant = createVariant(true, 0.0);
        long before = histories.count();
        assertThatThrownBy(() -> service.check("calculation.myshopify.com", variant.getId()))
                .isInstanceOf(ReorderNotReadyException.class);
        assertThat(histories.count()).isEqualTo(before);
    }

    private ProductVariant createVariant(boolean configured, double sales) {
        Store store = new Store("calculation-shop", "calculation.myshopify.com", "Calculation shop",
                "test-token", "calculation@example.com");
        if (configured) {
            store.configureReorderSettings(20, 5, 20);
        }
        stores.saveAndFlush(store);
        return variants.saveAndFlush(new ProductVariant(store, "calculation-product", "calculation-variant",
                "Product", "Variant", "SKU", 100, sales));
    }

    @Test
    void latestReturnsSavedSnapshotWithoutRecalculatingOrAddingHistory() {
        ProductVariant variant = createVariant(true, 10.0);
        service.check("calculation.myshopify.com", variant.getId());
        variant.getStore().configureReorderSettings(30, 5, 20);
        service.check("calculation.myshopify.com", variant.getId());
        // Current settings differ from the last calculation.
        variant.getStore().configureReorderSettings(40, 5, 20);
        entityManager.flush();
        entityManager.clear();
        long before = histories.count();
        var expected = histories.findTopByProductVariantOrderByCalculatedAtDescIdDesc(
                variants.findById(variant.getId()).orElseThrow()).orElseThrow();

        var result = service.findLatest("calculation.myshopify.com", variant.getId()).orElseThrow();

        assertThat(result.reorderPoint()).isEqualTo(350);
        assertThat(result.targetInventory()).isEqualTo(550);
        assertThat(result.shouldReorder()).isTrue();
        assertThat(result.recommendedQuantity()).isEqualTo(450);
        assertThat(result.calculatedAt()).isEqualTo(expected.getCalculatedAt());
        entityManager.flush();
        assertThat(histories.count()).isEqualTo(before);
    }

    @Test
    void latestIsEmptyForUnconfiguredVariantWithNoHistory() {
        ProductVariant variant = createVariant(false, 0.0);
        assertThat(service.findLatest("calculation.myshopify.com", variant.getId())).isEmpty();
    }

    @Test
    void latestRejectsVariantBelongingToAnotherStore() {
        ProductVariant variant = createVariant(true, 10.0);
        service.check("calculation.myshopify.com", variant.getId());
        assertThatThrownBy(() -> service.findLatest("other.myshopify.com", variant.getId()))
                .isInstanceOf(ProductVariantNotFoundException.class);
    }
}
