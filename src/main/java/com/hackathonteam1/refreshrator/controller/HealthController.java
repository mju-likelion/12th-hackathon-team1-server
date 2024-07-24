package com.hackathonteam1.refreshrator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")
    public String checkHealth(){
        return "ok";
    }
}