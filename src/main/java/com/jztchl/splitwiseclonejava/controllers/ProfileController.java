package com.jztchl.splitwiseclonejava.controllers;

import com.jztchl.splitwiseclonejava.models.Users;
import com.jztchl.splitwiseclonejava.services.JwtService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final JwtService jwtService;

    public ProfileController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping
    public Users getProfile() {
        return jwtService.getCurrentUser();
    }
}
