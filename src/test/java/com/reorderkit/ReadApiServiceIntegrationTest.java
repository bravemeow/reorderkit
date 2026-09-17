package com.reorderkit;

import com.reorderkit.entity.ProductVariant;
import com.reorderkit.entity.Store;
import com.reorderkit.exception.ProductVariantNotFoundException;
import com.reorderkit.exception.StoreNotFoundException;
import com.reorderkit.repository.ProductVariantRepository;
import com.reorderkit.repository.StoreRepository;
import com.reorderkit.service.ProductVariantConfigurationService;
import com.reorderkit.service.ProductVariantQueryService;
import com.reorderkit.service.StoreConfigurationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ProductVariantQueryService.class, ProductVariantConfigurationService.class, StoreConfigurationService.class})
class ReadApiServiceIntegrationTest {
    @Autowired private ProductVariantQueryService queryService;
    @Autowired private ProductVariantConfigurationService configurationService;
    @Autowired private StoreConfigurationService storeService;
    @Autowired private StoreRepository stores;
    @Autowired private ProductVariantRepository variants;
    @Autowired private TestEntityManager entityManager;

    @Test
    void pagesOnlyRequestedStoresProductsInStableIdOrder() {
        Store store = createStore("read");
        var first = createVariant(store, "first");
        var second = createVariant(store, "second");
        createVariant(createStore("other"), "other");
        entityManager.clear();
        var page0 = queryService.findAll(store.getShopDomain(), 0, 1);
        var page1 = queryService.findAll(store.getShopDomain(), 1, 1);
        assertThat(page0.totalElements()).isEqualTo(2);
        assertThat(page0.totalPages()).isEqualTo(2);
        assertThat(page0.items()).singleElement().satisfies(item -> {
            assertThat(item.variantId()).isEqualTo(first.getId());
            assertThat(item.inventoryQuantity()).isEqualTo(100);
            assertThat(item.averageDailySales30d()).isEqualTo(10.0);
        });
        assertThat(page1.items()).singleElement().satisfies(item ->
                assertThat(item.variantId()).isEqualTo(second.getId()));
        assertThat(queryService.findAll(store.getShopDomain(), 2, 1).items()).isEmpty();
    }

    @Test
    void emptyStoreIsDifferentFromMissingStore() {
        Store store = createStore("empty");
        assertThat(queryService.findAll(store.getShopDomain(), 0, 20).items()).isEmpty();
        assertThatThrownBy(() -> queryService.findAll("missing.myshopify.com", 0, 20))
                .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    void readsStoreDefaultsAndPreservesUnconfiguredState() {
        Store store = createStore("config");
        assertThat(storeService.getConfiguration(store.getShopDomain()).isConfigured()).isFalse();
        assertThat(storeService.getConfiguration(store.getShopDomain()).getLeadTimeDays()).isNull();
        store.configureReorderSettings(20, 5, 14);
        entityManager.flush();
        entityManager.clear();
        var response = storeService.getConfiguration(store.getShopDomain());
        assertThat(response.isConfigured()).isTrue();
        assertThat(response.getLeadTimeDays()).isEqualTo(20);
        assertThat(response.getBufferDays()).isEqualTo(5);
        assertThat(response.getOrderCoverageDays()).isEqualTo(14);
    }

    @Test
    void readsSavedPartialOverridesAndRejectsOtherStore() {
        Store store = createStore("override");
        var variant = createVariant(store, "override");
        variant.configureReorderOverrides(0, null, 14);
        entityManager.flush();
        entityManager.clear();
        var response = configurationService.getConfiguration(store.getShopDomain(), variant.getId());
        assertThat(response.leadTimeDays()).isZero();
        assertThat(response.bufferDays()).isNull();
        assertThat(response.orderCoverageDays()).isEqualTo(14);
        assertThatThrownBy(() -> configurationService.getConfiguration("other.myshopify.com", variant.getId()))
                .isInstanceOf(ProductVariantNotFoundException.class);
    }

    @Test
    void missingStoreConfigurationThrowsNotFound() {
        assertThatThrownBy(() -> storeService.getConfiguration("missing.myshopify.com"))
                .isInstanceOf(StoreNotFoundException.class);
    }

    private Store createStore(String name) {
        return stores.saveAndFlush(new Store("read-" + name, name + ".myshopify.com", name,
                "test-token", "test@example.com"));
    }

    private ProductVariant createVariant(Store store, String name) {
        return variants.saveAndFlush(new ProductVariant(store, "product-" + name, "variant-" + name,
                name, "Default", "SKU", 100, 10.0));
    }
}
