package com.reorderkit;

import com.reorderkit.entity.Store;
import com.reorderkit.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StoreRepositoryTest {

    @Autowired
    private StoreRepository repository;

    @Test
    void saveAndFindByShopDomain() {
        Store store = new Store("Test Shop Id",
                "Test Shop Domain",
                "Test Store Name",
                "Test Access Token",
                "Test Merchant Email");
        Store savedStore = repository.saveAndFlush(store);
        Optional<Store> foundStore = repository.findByShopDomain("Test Shop Domain");
        Store retrievedStore = foundStore.orElseThrow();
        assertThat(savedStore.getId()).isNotNull();
        assertThat(retrievedStore.getShopDomain()).isEqualTo("Test Shop Domain");
    }
}
