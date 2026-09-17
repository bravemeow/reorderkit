package com.reorderkit;

import com.reorderkit.controller.ProductVariantReorderController;
import com.reorderkit.dto.ReorderResponse;
import com.reorderkit.dto.LatestReorderResponse;
import java.time.Instant;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.reorderkit.exception.ProductVariantNotFoundException;
import com.reorderkit.exception.ReorderNotReadyException;
import com.reorderkit.service.ProductVariantReorderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductVariantReorderController.class)
class ProductVariantReorderControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private ProductVariantReorderService service;

    @Test
    void returnsLatestResultWithCalculationTime() throws Exception {
        Instant calculatedAt = Instant.parse("2026-09-16T12:00:00Z");
        when(service.findLatest("shop.myshopify.com", 1L)).thenReturn(Optional.of(
                new LatestReorderResponse(250, 450, false, 0, calculatedAt)));
        mockMvc.perform(get("/stores/shop.myshopify.com/variants/1/reorder/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reorderPoint").value(250))
                .andExpect(jsonPath("$.targetInventory").value(450))
                .andExpect(jsonPath("$.shouldReorder").value(false))
                .andExpect(jsonPath("$.recommendedQuantity").value(0))
                .andExpect(jsonPath("$.calculatedAt").value(calculatedAt.toString()));
    }

    @Test
    void returns204WhenVariantHasNoHistory() throws Exception {
        when(service.findLatest("shop.myshopify.com", 1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/stores/shop.myshopify.com/variants/1/reorder/latest"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void latestReturns404WhenVariantIsNotFound() throws Exception {
        when(service.findLatest("shop.myshopify.com", 1L))
                .thenThrow(new ProductVariantNotFoundException(1L));
        mockMvc.perform(get("/stores/shop.myshopify.com/variants/1/reorder/latest"))
                .andExpect(status().isNotFound());
    }

    @Test
    void calculatesWithoutRequestBody() throws Exception {
        when(service.check("shop.myshopify.com", 1L))
                .thenReturn(new ReorderResponse(250, 450, true, 350));
        mockMvc.perform(post("/stores/shop.myshopify.com/variants/1/reorder/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reorderPoint").value(250))
                .andExpect(jsonPath("$.targetInventory").value(450))
                .andExpect(jsonPath("$.shouldReorder").value(true))
                .andExpect(jsonPath("$.recommendedQuantity").value(350));
    }

    @Test
    void returns404ForUnknownVariant() throws Exception {
        when(service.check("shop.myshopify.com", 1L)).thenThrow(new ProductVariantNotFoundException(1L));
        mockMvc.perform(post("/stores/shop.myshopify.com/variants/1/reorder/check"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns409WhenConfigurationIsIncomplete() throws Exception {
        when(service.check("shop.myshopify.com", 1L))
                .thenThrow(new ReorderNotReadyException("Complete store configuration"));
        mockMvc.perform(post("/stores/shop.myshopify.com/variants/1/reorder/check"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Complete store configuration"));
    }
}
