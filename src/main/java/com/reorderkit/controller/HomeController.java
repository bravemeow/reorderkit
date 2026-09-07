package com.reorderkit.controller;

import com.reorderkit.dto.StatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public StatusResponse home() {
        return new StatusResponse("running");
    }
}
