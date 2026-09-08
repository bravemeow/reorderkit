package com.reorderkit.service;

import com.reorderkit.dto.ReorderRequest;
import com.reorderkit.dto.ReorderResponse;
import org.springframework.stereotype.Service;

@Service
public class ReorderService {

    // estimateStockOutDays = inventory / avgDailySales
    // reorderThreshold = leadTimeDays + bufferDays
    // shouldReorder if estimateStockOutDate <= reorderThreshold
    // recommendedQuantity = avgDailySales * (reorderCycleDays + bufferDays) - inventory


    public ReorderResponse check(ReorderRequest request) {
        int inventory = request.getInventory();
        double avgDailySales = request.getAverageDailySales30d();
        int leadTimeDays = request.getLeadTimeDays();
        int bufferDays = request.getBufferDays();
        int reorderCycleDays = request.getReorderCycleDays();

        boolean shouldReorder = false;
        if (avgDailySales <= 0) {   // validation
            throw new IllegalArgumentException("averageDailySales must be greater than 0");
        }
        int estimateStockOutDays = (int)(inventory / avgDailySales);
        int reorderThreshold = leadTimeDays + bufferDays;
        if(estimateStockOutDays <= reorderThreshold) {
            shouldReorder = true;
        }
        int recommendedQuantity = (int) Math.ceil(avgDailySales * (reorderCycleDays + bufferDays) - inventory);

        return new ReorderResponse(estimateStockOutDays, reorderThreshold, shouldReorder, recommendedQuantity);
    }
}
