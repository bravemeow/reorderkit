package com.reorderkit;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReorderController {

    @PostMapping("/reorder/check")
    public void reorderCheck(@RequestBody ReorderRequest request){

    }
}
