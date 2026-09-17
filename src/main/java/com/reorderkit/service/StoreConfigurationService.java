package com.reorderkit.service;

import com.reorderkit.dto.StoreConfigurationRequest;
import com.reorderkit.dto.StoreConfigurationResponse;
import com.reorderkit.entity.Store;
import com.reorderkit.exception.StoreNotFoundException;
import com.reorderkit.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreConfigurationService {
    private final StoreRepository storeRepository;

    public StoreConfigurationService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public StoreConfigurationResponse getConfiguration(String shopDomain) {
        Store store = storeRepository.findByShopDomain(shopDomain)
                .orElseThrow(() -> new StoreNotFoundException(shopDomain));
        return new StoreConfigurationResponse(store.getShopDomain(), store.getDefaultLeadTimeDays(),
                store.getDefaultBufferDays(), store.getDefaultOrderCoverageDays(), store.isConfigured());
    }

    @Transactional
    public StoreConfigurationResponse updateConfiguration(
            String shopDomain,
            StoreConfigurationRequest request
    ) {
        Store store = storeRepository.findByShopDomain(shopDomain)
                .orElseThrow(() -> new StoreNotFoundException(shopDomain));

        store.configureReorderSettings(
                request.getLeadTimeDays(),
                request.getBufferDays(),
                request.getOrderCoverageDays()
        );

        return new StoreConfigurationResponse(
                store.getShopDomain(),
                store.getDefaultLeadTimeDays(),
                store.getDefaultBufferDays(),
                store.getDefaultOrderCoverageDays(),
                store.isConfigured()
        );
    }
}
