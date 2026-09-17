package com.reorderkit;

import com.reorderkit.controller.ProductVariantConfigurationController;
import com.reorderkit.dto.ProductVariantConfigurationRequest;
import com.reorderkit.dto.ProductVariantConfigurationResponse;
import com.reorderkit.exception.ProductVariantNotFoundException;
import com.reorderkit.service.ProductVariantConfigurationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductVariantConfigurationController.class)
class ProductVariantConfigurationControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private ProductVariantConfigurationService service;
    private static final String URL = "/stores/shop.myshopify.com/variants/1/configuration";

    @Test
    void acceptsPartialOverridesAndReturnsNullForInheritedFields() throws Exception {
        var request = new ProductVariantConfigurationRequest(0, null, 14);
        when(service.updateConfiguration("shop.myshopify.com", 1L, request))
                .thenReturn(new ProductVariantConfigurationResponse(1L, 0, null, 14));
        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"leadTimeDays\":0,\"bufferDays\":null,\"orderCoverageDays\":14}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.variantId").value(1))
                .andExpect(jsonPath("$.leadTimeDays").value(0))
                .andExpect(jsonPath("$.bufferDays").value(nullValue()))
                .andExpect(jsonPath("$.orderCoverageDays").value(14));
        verify(service).updateConfiguration("shop.myshopify.com", 1L, request);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"leadTimeDays\":null,\"bufferDays\":null,\"orderCoverageDays\":null}"})
    void missingAndNullFieldsBothClearOverrides(String json) throws Exception {
        var request = new ProductVariantConfigurationRequest(null, null, null);
        when(service.updateConfiguration("shop.myshopify.com", 1L, request))
                .thenReturn(new ProductVariantConfigurationResponse(1L, null, null, null));
        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
        verify(service).updateConfiguration("shop.myshopify.com", 1L, request);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"leadTimeDays\":-1}", "{\"bufferDays\":-1}",
            "{\"orderCoverageDays\":-1}", "{\"orderCoverageDays\":0}"})
    void rejectsInvalidValuesBeforeCallingService(String json) throws Exception {
        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void returns404ForVariantOutsideStore() throws Exception {
        when(service.updateConfiguration(eq("shop.myshopify.com"), eq(1L), any()))
                .thenThrow(new ProductVariantNotFoundException(1L));
        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());
    }
}
