package com.reorderkit;

import com.reorderkit.controller.ProductVariantConfigurationController;
import com.reorderkit.controller.ProductVariantQueryController;
import com.reorderkit.controller.StoreConfigurationController;
import com.reorderkit.dto.*;
import com.reorderkit.exception.ProductVariantNotFoundException;
import com.reorderkit.exception.StoreNotFoundException;
import com.reorderkit.service.ProductVariantConfigurationService;
import com.reorderkit.service.ProductVariantQueryService;
import com.reorderkit.service.StoreConfigurationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ProductVariantQueryController.class, StoreConfigurationController.class,
        ProductVariantConfigurationController.class})
class ReadApiControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private ProductVariantQueryService queryService;
    @MockitoBean private StoreConfigurationService storeService;
    @MockitoBean private ProductVariantConfigurationService configurationService;
    private static final String SHOP = "read.myshopify.com";
    private static final String BASE = "/stores/" + SHOP;

    @Test
    void listsPublicProductFieldsWithDefaultPagination() throws Exception {
        var item = new ProductVariantResponse(1L, "product", "variant", "Hat", "Blue", "SKU",
                100, 0, 10.0, null);
        when(queryService.findAll(SHOP, 0, 20))
                .thenReturn(new ProductVariantPageResponse(List.of(item), 0, 20, 1, 1));
        mockMvc.perform(get(BASE + "/variants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].variantId").value(1))
                .andExpect(jsonPath("$.items[0].productTitle").value("Hat"))
                .andExpect(jsonPath("$.items[0].inventoryQuantity").value(100))
                .andExpect(jsonPath("$.items[0].store").doesNotExist())
                .andExpect(jsonPath("$.items[0].accessToken").doesNotExist())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void acceptsPaginationAndReturnsEmptyPage() throws Exception {
        when(queryService.findAll(SHOP, 1, 5))
                .thenReturn(new ProductVariantPageResponse(List.of(), 1, 5, 0, 0));
        mockMvc.perform(get(BASE + "/variants?page=1&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
        verify(queryService).findAll(SHOP, 1, 5);
    }

    @ParameterizedTest
    @ValueSource(strings = {"?page=-1", "?size=0", "?size=101", "?page=abc"})
    void rejectsInvalidPagination(String query) throws Exception {
        mockMvc.perform(get(BASE + "/variants" + query)).andExpect(status().isBadRequest());
        verifyNoInteractions(queryService);
    }

    @Test
    void listReturns404ForMissingStore() throws Exception {
        when(queryService.findAll(SHOP, 0, 20)).thenThrow(new StoreNotFoundException(SHOP));
        mockMvc.perform(get(BASE + "/variants")).andExpect(status().isNotFound());
    }

    @Test
    void readsUnconfiguredStoreWithNullSettings() throws Exception {
        when(storeService.getConfiguration(SHOP))
                .thenReturn(new StoreConfigurationResponse(SHOP, null, null, null, false));
        mockMvc.perform(get(BASE + "/configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configured").value(false))
                .andExpect(jsonPath("$.leadTimeDays").value(nullValue()))
                .andExpect(jsonPath("$.accessToken").doesNotExist());
    }

    @Test
    void storeConfigurationReturns404ForMissingStore() throws Exception {
        when(storeService.getConfiguration(SHOP)).thenThrow(new StoreNotFoundException(SHOP));
        mockMvc.perform(get(BASE + "/configuration")).andExpect(status().isNotFound());
    }

    @Test
    void readsOverridesWithoutReplacingNullWithDefault() throws Exception {
        when(configurationService.getConfiguration(SHOP, 1L))
                .thenReturn(new ProductVariantConfigurationResponse(1L, 10, null, 14));
        mockMvc.perform(get(BASE + "/variants/1/configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leadTimeDays").value(10))
                .andExpect(jsonPath("$.bufferDays").value(nullValue()))
                .andExpect(jsonPath("$.orderCoverageDays").value(14));
    }

    @Test
    void variantConfigurationReturns404ForWrongStore() throws Exception {
        when(configurationService.getConfiguration(SHOP, 1L))
                .thenThrow(new ProductVariantNotFoundException(1L));
        mockMvc.perform(get(BASE + "/variants/1/configuration")).andExpect(status().isNotFound());
    }
}
