package com.reorderkit.controller;

import com.reorderkit.dto.StoreConfigurationRequest;
import com.reorderkit.dto.StoreConfigurationResponse;
import com.reorderkit.service.StoreConfigurationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stores")
public class StoreConfigurationController {
    private final StoreConfigurationService storeConfigurationService;

    public StoreConfigurationController(StoreConfigurationService storeConfigurationService) {
        this.storeConfigurationService = storeConfigurationService;
    }

    @GetMapping("/{shopDomain}/configuration")
    public StoreConfigurationResponse getConfiguration(@PathVariable String shopDomain) {
        return storeConfigurationService.getConfiguration(shopDomain);
    }

    @PutMapping("/{shopDomain}/configuration")
    public StoreConfigurationResponse updateConfiguration(
            @PathVariable String shopDomain,
            @Valid @RequestBody StoreConfigurationRequest request
    ) {
        return storeConfigurationService.updateConfiguration(shopDomain, request);
    }
}
