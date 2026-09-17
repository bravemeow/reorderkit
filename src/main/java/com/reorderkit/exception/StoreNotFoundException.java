package com.reorderkit.exception;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(String shopDomain) {
        super("Store not found for shop domain: " + shopDomain);
    }
}
