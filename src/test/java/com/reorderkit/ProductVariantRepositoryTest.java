package com.reorderkit;

import com.reorderkit.entity.ProductVariant;
import com.reorderkit.entity.Store;
import com.reorderkit.repository.ProductVariantRepository;
import com.reorderkit.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductVariantRepositoryTest {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void saveAndFindByShopifyVariantId() {
        Store store = storeRepository.saveAndFlush(new Store(
                "Test Shop Id",
                "test-shop.myshopify.com",
                "Test Store",
                "Test Access Token",
                "merchant@example.com"
        ));

        ProductVariant productVariant = new ProductVariant(
                store,
                "Test Product Id",
                "Test Variant Id",
                "Test Product Title",
                "Test Variant Title",
                "Test SKU",
                100,
                20.0
        );

        ProductVariant savedVariant = productVariantRepository.saveAndFlush(productVariant);
        Optional<ProductVariant> foundProductVariant =
                productVariantRepository.findByShopifyVariantId("Test Variant Id");

        assertThat(savedVariant.getId()).isNotNull();
        assertThat(foundProductVariant).isPresent();

        ProductVariant retrievedVariant = foundProductVariant.orElseThrow();
        assertThat(retrievedVariant.getShopifyVariantId()).isEqualTo("Test Variant Id");
        assertThat(retrievedVariant.getStore().getId()).isEqualTo(store.getId());
        assertThat(retrievedVariant.getOnOrderQuantity()).isZero();
        assertThat(retrievedVariant.getAverageDailySales30d()).isEqualTo(20.0);
    }

    @Test
    void findAllByStoreReturnsOnlyVariantsOwnedByThatStore() {
        Store firstStore = storeRepository.saveAndFlush(new Store(
                "First Shop Id",
                "first-shop.myshopify.com",
                "First Store",
                "First Access Token",
                "first@example.com"
        ));
        Store secondStore = storeRepository.saveAndFlush(new Store(
                "Second Shop Id",
                "second-shop.myshopify.com",
                "Second Store",
                "Second Access Token",
                "second@example.com"
        ));

        ProductVariant firstVariant = new ProductVariant(
                firstStore,
                "First Product Id",
                "First Variant Id",
                "First Product",
                "Default",
                "SKU-1",
                100,
                10.0
        );
        ProductVariant secondVariant = new ProductVariant(
                firstStore,
                "Second Product Id",
                "Second Variant Id",
                "Second Product",
                "Default",
                "SKU-2",
                200,
                20.0
        );
        ProductVariant otherStoreVariant = new ProductVariant(
                secondStore,
                "Other Product Id",
                "Other Variant Id",
                "Other Product",
                "Default",
                "SKU-3",
                300,
                30.0
        );

        productVariantRepository.saveAllAndFlush(List.of(
                firstVariant,
                secondVariant,
                otherStoreVariant
        ));

        List<ProductVariant> foundVariants =
                productVariantRepository.findAllByStore(firstStore);

        assertThat(foundVariants)
                .hasSize(2)
                .extracting(ProductVariant::getShopifyVariantId)
                .containsExactlyInAnyOrder("First Variant Id", "Second Variant Id");
        assertThat(foundVariants)
                .allSatisfy(variant ->
                        assertThat(variant.getStore().getId()).isEqualTo(firstStore.getId())
                );

    }
}
