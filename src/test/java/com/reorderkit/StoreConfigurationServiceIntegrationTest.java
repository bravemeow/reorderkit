package com.reorderkit;

import com.reorderkit.dto.StoreConfigurationRequest;
import com.reorderkit.dto.StoreConfigurationResponse;
import com.reorderkit.entity.Store;
import com.reorderkit.exception.StoreNotFoundException;
import com.reorderkit.repository.StoreRepository;
import com.reorderkit.service.StoreConfigurationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(StoreConfigurationService.class)
class StoreConfigurationServiceIntegrationTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreConfigurationService storeConfigurationService;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void updateConfigurationUsesDirtyChecking() {
        storeRepository.saveAndFlush(new Store(
                "Configuration Shop Id",
                "configuration-shop.myshopify.com",
                "Configuration Store",
                "Configuration Access Token",
                "configuration@example.com"
        ));

        StoreConfigurationRequest request = new StoreConfigurationRequest();
        request.setLeadTimeDays(10);
        request.setBufferDays(5);
        request.setOrderCoverageDays(14);

        StoreConfigurationResponse response =
                storeConfigurationService.updateConfiguration(
                        "configuration-shop.myshopify.com",
                        request
                );

        entityManager.flush();
        entityManager.clear();

        Store updatedStore = storeRepository
                .findByShopDomain("configuration-shop.myshopify.com")
                .orElseThrow();

        assertThat(response.isConfigured()).isTrue();
        assertThat(response.getLeadTimeDays()).isEqualTo(10);
        assertThat(response.getBufferDays()).isEqualTo(5);
        assertThat(response.getOrderCoverageDays()).isEqualTo(14);

        assertThat(updatedStore.isConfigured()).isTrue();
        assertThat(updatedStore.getDefaultLeadTimeDays()).isEqualTo(10);
        assertThat(updatedStore.getDefaultBufferDays()).isEqualTo(5);
        assertThat(updatedStore.getDefaultOrderCoverageDays()).isEqualTo(14);
    }

    @Test
    void updateConfigurationThrowsWhenStoreDoesNotExist() {
        StoreConfigurationRequest request = new StoreConfigurationRequest();
        request.setLeadTimeDays(10);
        request.setBufferDays(5);
        request.setOrderCoverageDays(14);

        assertThatThrownBy(() ->
                storeConfigurationService.updateConfiguration(
                        "missing-shop.myshopify.com",
                        request
                )
        )
                .isInstanceOf(StoreNotFoundException.class)
                .hasMessageContaining("missing-shop.myshopify.com");
    }
}
