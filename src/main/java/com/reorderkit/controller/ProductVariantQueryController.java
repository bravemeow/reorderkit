package com.reorderkit.controller;

import com.reorderkit.dto.ProductVariantPageResponse;
import com.reorderkit.service.ProductVariantQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ProductVariantQueryController {
    private final ProductVariantQueryService service;

    public ProductVariantQueryController(ProductVariantQueryService service) {
        this.service = service;
    }

    @GetMapping("/stores/{shopDomain}/variants")
    public ProductVariantPageResponse findAll(@PathVariable String shopDomain,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be >= 0; size must be 1..100");
        }
        return service.findAll(shopDomain, page, size);
    }
}
