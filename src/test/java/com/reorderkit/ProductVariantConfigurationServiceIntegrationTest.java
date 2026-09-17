package com.reorderkit;

import com.reorderkit.dto.ProductVariantConfigurationRequest;
import com.reorderkit.entity.ProductVariant;
import com.reorderkit.entity.Store;
import com.reorderkit.exception.ProductVariantNotFoundException;
import com.reorderkit.repository.ProductVariantRepository;
import com.reorderkit.repository.ReorderHistoryRepository;
import com.reorderkit.repository.StoreRepository;
import com.reorderkit.service.ProductVariantConfigurationService;
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
@Import({ProductVariantConfigurationService.class, ProductVariantReorderService.class, ReorderService.class})
class ProductVariantConfigurationServiceIntegrationTest {
    @Autowired private ProductVariantConfigurationService service;
    @Autowired private ProductVariantReorderService reorderService;
    @Autowired private ProductVariantRepository variants;
    @Autowired private StoreRepository stores;
    @Autowired private ReorderHistoryRepository histories;
    @Autowired private TestEntityManager entityManager;
    private static final String SHOP = "override-test.myshopify.com";

    @Test
    void persistsOverridesByDirtyCheckingWithoutChangingStoreOrCreatingHistory() {
        Long id = createVariant();
        long historyCount = histories.count();
        service.updateConfiguration(SHOP, id, new ProductVariantConfigurationRequest(10, 0, 14));
        entityManager.flush();
        entityManager.clear();
        var variant = variants.findById(id).orElseThrow();
        assertThat(variant.getLeadTimeDaysOverride()).isEqualTo(10);
        assertThat(variant.getBufferDaysOverride()).isZero();
        assertThat(variant.getOrderCoverageDaysOverride()).isEqualTo(14);
        assertThat(variant.getStore().getDefaultLeadTimeDays()).isEqualTo(20);
        assertThat(variant.getStore().getDefaultBufferDays()).isEqualTo(5);
        assertThat(variant.getStore().getDefaultOrderCoverageDays()).isEqualTo(20);
        assertThat(histories.count()).isEqualTo(historyCount);
    }

    @Test
    void clearingOverridesUsesChangedStoreDefaultsOnNextCalculation() {
        Long id = createVariant();
        service.updateConfiguration(SHOP, id, new ProductVariantConfigurationRequest(10, 0, 14));
        assertThat(reorderService.check(SHOP, id).getReorderPoint()).isEqualTo(100);
        service.updateConfiguration(SHOP, id, new ProductVariantConfigurationRequest(null, null, null));
        stores.findByShopDomain(SHOP).orElseThrow().configureReorderSettings(30, 5, 20);
        entityManager.flush();
        entityManager.clear();
        var variant = variants.findById(id).orElseThrow();
        assertThat(variant.getLeadTimeDaysOverride()).isNull();
        assertThat(variant.getBufferDaysOverride()).isNull();
        assertThat(variant.getOrderCoverageDaysOverride()).isNull();
        var result = reorderService.check(SHOP, id);
        assertThat(result.getReorderPoint()).isEqualTo(350);
        assertThat(result.getRecommendedQuantity()).isEqualTo(450);
    }

    @Test
    void partialReplacementClearsOtherOverrides() {
        Long id = createVariant();
        service.updateConfiguration(SHOP, id, new ProductVariantConfigurationRequest(10, 0, 14));
        service.updateConfiguration(SHOP, id, new ProductVariantConfigurationRequest(7, null, null));
        entityManager.flush();
        entityManager.clear();
        var variant = variants.findById(id).orElseThrow();
        assertThat(variant.getLeadTimeDaysOverride()).isEqualTo(7);
        assertThat(variant.getBufferDaysOverride()).isNull();
        assertThat(variant.getOrderCoverageDaysOverride()).isNull();
    }

    @Test
    void cannotUpdateVariantFromAnotherStore() {
        Long id = createVariant();
        assertThatThrownBy(() -> service.updateConfiguration("other.myshopify.com", id,
                new ProductVariantConfigurationRequest(10, null, null)))
                .isInstanceOf(ProductVariantNotFoundException.class);
        entityManager.flush();
        entityManager.clear();
        assertThat(variants.findById(id).orElseThrow().getLeadTimeDaysOverride()).isNull();
    }

    private Long createVariant() {
        Store store = new Store("override-shop", SHOP, "Override store", "test-token", "test@example.com");
        store.configureReorderSettings(20, 5, 20);
        stores.saveAndFlush(store);
        return variants.saveAndFlush(new ProductVariant(store, "override-product", "override-variant",
                "Product", "Variant", "SKU", 100, 10.0)).getId();
    }
}
