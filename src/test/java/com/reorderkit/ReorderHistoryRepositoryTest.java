package com.reorderkit;

import com.reorderkit.entity.ProductVariant;
import com.reorderkit.entity.ReorderHistory;
import com.reorderkit.entity.Store;
import com.reorderkit.repository.ProductVariantRepository;
import com.reorderkit.repository.ReorderHistoryRepository;
import com.reorderkit.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReorderHistoryRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ReorderHistoryRepository reorderHistoryRepository;

    @Test
    void findLatestHistoryForProductVariant() {
        ProductVariant productVariant = saveProductVariant("latest");

        ReorderHistory firstHistory = reorderHistoryRepository.saveAndFlush(
                createHistory(productVariant, 100, ReorderHistory.CalculationTrigger.MANUAL_REFRESH)
        );
        ReorderHistory secondHistory = reorderHistoryRepository.saveAndFlush(
                createHistory(productVariant, 200, ReorderHistory.CalculationTrigger.SCHEDULED)
        );

        Optional<ReorderHistory> latestHistory =
                reorderHistoryRepository.findTopByProductVariantOrderByCalculatedAtDescIdDesc(
                        productVariant
                );

        assertThat(firstHistory.getId()).isNotNull();
        assertThat(secondHistory.getId()).isNotNull();
        assertThat(latestHistory).isPresent();

        ReorderHistory retrievedHistory = latestHistory.orElseThrow();
        assertThat(retrievedHistory.getId()).isEqualTo(secondHistory.getId());
        assertThat(retrievedHistory.getRecommendedQuantity()).isEqualTo(200);
        assertThat(retrievedHistory.getCalculationTrigger())
                .isEqualTo(ReorderHistory.CalculationTrigger.SCHEDULED);
    }

    @Test
    void findHistoryPageInLatestFirstOrder() {
        ProductVariant productVariant = saveProductVariant("page");

        ReorderHistory firstHistory = reorderHistoryRepository.saveAndFlush(
                createHistory(productVariant, 100, ReorderHistory.CalculationTrigger.MANUAL_REFRESH)
        );
        ReorderHistory secondHistory = reorderHistoryRepository.saveAndFlush(
                createHistory(productVariant, 200, ReorderHistory.CalculationTrigger.SCHEDULED)
        );
        ReorderHistory thirdHistory = reorderHistoryRepository.saveAndFlush(
                createHistory(productVariant, 300, ReorderHistory.CalculationTrigger.MANUAL_REFRESH)
        );

        Page<ReorderHistory> historyPage =
                reorderHistoryRepository.findAllByProductVariantOrderByCalculatedAtDescIdDesc(
                        productVariant,
                        PageRequest.of(0, 2)
                );

        assertThat(historyPage.getTotalElements()).isEqualTo(3);
        assertThat(historyPage.getTotalPages()).isEqualTo(2);
        assertThat(historyPage.getContent())
                .extracting(ReorderHistory::getId)
                .containsExactly(thirdHistory.getId(), secondHistory.getId());
        assertThat(historyPage.getContent())
                .extracting(ReorderHistory::getRecommendedQuantity)
                .containsExactly(300, 200);
        assertThat(firstHistory.getId()).isNotIn(
                historyPage.getContent().stream().map(ReorderHistory::getId).toList()
        );
    }

    private ProductVariant saveProductVariant(String suffix) {
        Store store = storeRepository.saveAndFlush(new Store(
                "Shop Id " + suffix,
                suffix + ".myshopify.com",
                "Store " + suffix,
                "Access Token " + suffix,
                suffix + "@example.com"
        ));

        return productVariantRepository.saveAndFlush(new ProductVariant(
                store,
                "Product Id " + suffix,
                "Variant Id " + suffix,
                "Product " + suffix,
                "Default",
                "SKU-" + suffix,
                100,
                10.0
        ));
    }

    private ReorderHistory createHistory(
            ProductVariant productVariant,
            int recommendedQuantity,
            ReorderHistory.CalculationTrigger calculationTrigger
    ) {
        return new ReorderHistory(
                productVariant,
                100,
                0,
                10.0,
                10,
                5,
                10,
                150,
                250,
                true,
                recommendedQuantity,
                calculationTrigger
        );
    }
}
