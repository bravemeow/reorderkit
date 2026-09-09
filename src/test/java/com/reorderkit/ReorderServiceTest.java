package com.reorderkit;

import com.reorderkit.dto.ReorderRequest;
import com.reorderkit.dto.ReorderResponse;
import com.reorderkit.service.ReorderService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReorderServiceTest {
    ReorderService rs = new ReorderService();

    private ReorderRequest makeRequest(Integer inventory, Integer onOrderQuantity, Double setAverageDailySales, Integer leadTimeDays, Integer bufferDays, Integer orderCoverageDays) {
        ReorderRequest request = new ReorderRequest();
        request.setInventory(inventory);
        request.setOnOrderQuantity(onOrderQuantity);
        request.setAverageDailySales(setAverageDailySales);
        request.setLeadTimeDays(leadTimeDays);
        request.setBufferDays(bufferDays);
        request.setOrderCoverageDays(orderCoverageDays);
        return request;
    }

    @Test
    public void shouldReorderTest() {
        ReorderRequest request = makeRequest(100, 0, 10.0, 20, 5, 20);
        ReorderResponse response = rs.check(request);

        assertEquals(250, response.getReorderPoint());
        assertEquals(450, response.getTargetInventory());
        assertTrue(response.isShouldReorder());
        assertEquals(350, response.getRecommendedQuantity());
    }

    @Test
    public void shouldNotReorderTest() {
        ReorderRequest request = makeRequest(300, 0, 10.0, 20, 5, 20);
        ReorderResponse response = rs.check(request);

        assertEquals(250, response.getReorderPoint());
        assertEquals(450, response.getTargetInventory());
        assertFalse(response.isShouldReorder());
        assertEquals(0, response.getRecommendedQuantity());
    }

    @Test
    public void emptyInventoryReorderTest() {
        ReorderRequest request = makeRequest(0, 0, 10.0, 20, 5, 20);
        ReorderResponse response = rs.check(request);

        assertEquals(250, response.getReorderPoint());
        assertEquals(450, response.getTargetInventory());
        assertTrue(response.isShouldReorder());
        assertEquals(450, response.getRecommendedQuantity());
    }

    @Test
    public void nullReorderTest() {
        ReorderRequest request = makeRequest(0, null, 10.0, 20, 5, 20);
        ReorderResponse response = rs.check(request);

        assertEquals(250, response.getReorderPoint());
        assertEquals(450, response.getTargetInventory());
        assertTrue(response.isShouldReorder());
        assertEquals(450, response.getRecommendedQuantity());
    }

    @Test
    public void onOrderReorderTest() {
        ReorderRequest request = makeRequest(200, 100, 10.0, 20, 5, 20);
        ReorderResponse response = rs.check(request);

        assertEquals(250, response.getReorderPoint());
        assertEquals(450, response.getTargetInventory());
        assertFalse(response.isShouldReorder());
        assertEquals(0, response.getRecommendedQuantity());
    }

    @Test
    public void equalInventoryAndReorderPointReorderTest() {
        ReorderRequest request = makeRequest(250, 0, 10.0, 20, 5, 20);
        ReorderResponse response = rs.check(request);

        assertEquals(250, response.getReorderPoint());
        assertEquals(450, response.getTargetInventory());
        assertTrue(response.isShouldReorder());
        assertEquals(200, response.getRecommendedQuantity());
    }

    @Test
    public void negativeInventoryReorderTest() {
        ReorderRequest request = makeRequest(-11, 100, 10.0, 20, 5, 20);
        assertThrows(IllegalArgumentException.class, () -> rs.check(request));
    }

    @Test
    public void negativeOnOrderQuantityReorderTest() {
        ReorderRequest request = makeRequest(0, -10, 10.0, 20, 5, 20);
        assertThrows(IllegalArgumentException.class, () -> rs.check(request));
    }

    @Test
    public void negativeAverageDailySales30dReorderTest() {
        ReorderRequest request = makeRequest(0, 100, -10.0, 20, 5, 20);
        assertThrows(IllegalArgumentException.class, () -> rs.check(request));
    }

    @Test
    public void negativeLeadTimeDaysReorderTest() {
        ReorderRequest request = makeRequest(0, 100, 10.0, -20, 5, 20);
        assertThrows(IllegalArgumentException.class, () -> rs.check(request));
    }

    @Test
    public void negativeBufferDaysReorderTest() {
        ReorderRequest request = makeRequest(0, 100, 10.0, 20, -5, 20);
        assertThrows(IllegalArgumentException.class, () -> rs.check(request));
    }

    @Test
    public void negativeOrderCoverageDaysReorderTest() {
        ReorderRequest request = makeRequest(0, 100, 10.0, 20, 5, -20);
        assertThrows(IllegalArgumentException.class, () -> rs.check(request));
    }
}
