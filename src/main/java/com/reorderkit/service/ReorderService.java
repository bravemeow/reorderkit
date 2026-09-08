package com.reorderkit.service;

import com.reorderkit.dto.ReorderRequest;
import com.reorderkit.dto.ReorderResponse;
import org.springframework.stereotype.Service;

@Service
public class ReorderService {
    // inventoryPosition = currentInventory + onOrderQuantity
    // reorderPoint = averageDailySales * (leadTimeDays + bufferDays)
    // shouldReorder = inventoryPosition <= reorderPoint
    // targetInventory = reorderPoint + (averageDailySales30d * orderCoverageDays)
    // recommendedQuantity = max(0, targetInventory - inventoryPosition)


    public ReorderResponse check(ReorderRequest request) {
        validateRequest(request);

        int inventory = request.getInventory();
        int onOrderQuantity = request.getOnOrderQuantity() == null ? 0 : request.getOnOrderQuantity();
        int inventoryPosition = inventory + onOrderQuantity;
        double avgDailySales = request.getAverageDailySales();
        int leadTimeDays = request.getLeadTimeDays();
        int bufferDays = request.getBufferDays();
        int orderCoverageDays = request.getOrderCoverageDays();

        double reorderPoint = avgDailySales * (leadTimeDays + bufferDays);
        int targetInventory = (int)(reorderPoint + (avgDailySales * orderCoverageDays));
        boolean shouldReorder = inventoryPosition <= reorderPoint;
        int recommendedQuantity = 0;
        if(shouldReorder){
            recommendedQuantity = Math.max(0, targetInventory - inventoryPosition);
        }

        return new ReorderResponse((int) reorderPoint, targetInventory, shouldReorder, recommendedQuantity);
    }

    private void validateRequest(ReorderRequest request) {
        if (request.getInventory() < 0) {
            throw new IllegalArgumentException(
                    "inventory must not be negative"
            );
        }

        if (request.getOnOrderQuantity() != null
                && request.getOnOrderQuantity() < 0) {
            throw new IllegalArgumentException(
                    "onOrderQuantity must not be negative"
            );
        }

        if (request.getAverageDailySales() <= 0) {
            throw new IllegalArgumentException(
                    "averageDailySales must be greater than 0"
            );
        }

        if (request.getLeadTimeDays() < 0) {
            throw new IllegalArgumentException(
                    "leadTimeDays must not be negative"
            );
        }

        if (request.getBufferDays() < 0) {
            throw new IllegalArgumentException(
                    "bufferDays must not be negative"
            );
        }

        if (request.getOrderCoverageDays() < 0) {
            throw new IllegalArgumentException(
                    "orderCoverageDays must not be negative"
            );
        }
    }
}
