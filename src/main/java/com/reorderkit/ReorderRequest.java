package com.reorderkit;

public class ReorderRequest {
    int inventory;
    double averageDailySales;

    public ReorderRequest(int inventory, double averageDailySales){
        this.inventory = inventory;
        this.averageDailySales = averageDailySales;
    }

    public int getInventory() {
        return inventory;
    }

    public double getAverageDailySales() {
        return averageDailySales;
    }
}
