package com.reorderkit.controller;

import com.reorderkit.dto.ReorderRequest;
import com.reorderkit.dto.ReorderResponse;
import com.reorderkit.service.ReorderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReorderController {
    private final ReorderService reorderService;

    public ReorderController(ReorderService reorderService){
        this.reorderService = reorderService;
    }

    @PostMapping("/reorder/check")
    public ReorderResponse reorderCheck(@Valid @RequestBody ReorderRequest request){
        return reorderService.check(request);
    }
}
