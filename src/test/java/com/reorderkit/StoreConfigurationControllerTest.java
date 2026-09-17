package com.reorderkit;

import com.reorderkit.controller.StoreConfigurationController;
import com.reorderkit.dto.StoreConfigurationRequest;
import com.reorderkit.dto.StoreConfigurationResponse;
import com.reorderkit.exception.StoreNotFoundException;
import com.reorderkit.service.StoreConfigurationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreConfigurationController.class)
class StoreConfigurationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StoreConfigurationService storeConfigurationService;

    @Test
    void updateConfigurationReturnsUpdatedSettings() throws Exception {
        when(storeConfigurationService.updateConfiguration(
                eq("test-shop.myshopify.com"),
                any(StoreConfigurationRequest.class)
        )).thenReturn(new StoreConfigurationResponse(
                "test-shop.myshopify.com",
                10,
                5,
                14,
                true
        ));

        mockMvc.perform(put("/stores/test-shop.myshopify.com/configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "leadTimeDays": 10,
                                  "bufferDays": 5,
                                  "orderCoverageDays": 14
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shopDomain").value("test-shop.myshopify.com"))
                .andExpect(jsonPath("$.leadTimeDays").value(10))
                .andExpect(jsonPath("$.bufferDays").value(5))
                .andExpect(jsonPath("$.orderCoverageDays").value(14))
                .andExpect(jsonPath("$.configured").value(true));
    }

    @Test
    void updateConfigurationRejectsInvalidRequest() throws Exception {
        mockMvc.perform(put("/stores/test-shop.myshopify.com/configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "leadTimeDays": -1,
                                  "bufferDays": 5,
                                  "orderCoverageDays": 0
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(storeConfigurationService);
    }

    @Test
    void updateConfigurationReturnsNotFoundForUnknownStore() throws Exception {
        when(storeConfigurationService.updateConfiguration(
                eq("missing-shop.myshopify.com"),
                any(StoreConfigurationRequest.class)
        )).thenThrow(new StoreNotFoundException("missing-shop.myshopify.com"));

        mockMvc.perform(put("/stores/missing-shop.myshopify.com/configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "leadTimeDays": 10,
                                  "bufferDays": 5,
                                  "orderCoverageDays": 14
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Store not found for shop domain: missing-shop.myshopify.com"));
    }
}
