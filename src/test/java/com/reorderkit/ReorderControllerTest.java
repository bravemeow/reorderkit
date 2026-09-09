package com.reorderkit;

import com.reorderkit.controller.ReorderController;
import com.reorderkit.dto.ReorderRequest;
import com.reorderkit.dto.ReorderResponse;
import com.reorderkit.service.ReorderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReorderController.class)
public class ReorderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReorderService reorderService;

    @Test
    void reorderControllerTest() throws Exception{
        when(reorderService.check(any(ReorderRequest.class)))
                .thenReturn(new ReorderResponse(250,450, true, 350));

        mockMvc.perform(post("/reorder/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "inventory": 100,
                              "onOrderQuantity": 0,
                              "averageDailySales": 10.0,
                              "leadTimeDays": 20,
                              "bufferDays": 5,
                              "orderCoverageDays": 20
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reorderPoint").value(250))
                .andExpect(jsonPath("$.targetInventory").value(450))
                .andExpect(jsonPath("$.shouldReorder").value(true))
                .andExpect(jsonPath("$.recommendedQuantity").value(350));
    }
}
