package com.reorderkit.exception;

public class ProductVariantNotFoundException extends RuntimeException {
    public ProductVariantNotFoundException(Long variantId) {
        super("Product variant not found in this store: " + variantId);
    }
}
