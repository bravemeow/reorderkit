package com.reorderkit;

import com.reorderkit.dto.ReorderRequest;
import com.reorderkit.dto.ReorderResponse;
import com.reorderkit.service.ReorderService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReorderServiceTest {
    ReorderService rs = new ReorderService();

    @Test
    public void serviceTest() {
        ReorderRequest request = new ReorderRequest();
        request.setInventory(100);
        request.setAverageDailySales30d(10.0);
        request.setLeadTimeDays(20);
        request.setBufferDays(5);
        request.setReorderCycleDays(30);


        ReorderResponse response = rs.check(request);

        assertEquals(10, response.getEstimateStockOutDays());
        assertEquals(25, response.getReorderThreshold());
        assertTrue(response.isShouldReorder());
        assertEquals(250, response.getRecommendedQuantity());
    }
}
