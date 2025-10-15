package com.jztchl.splitwiseclonejava.controllers;

import com.jztchl.splitwiseclonejava.dtos.ProfileDto;
import com.jztchl.splitwiseclonejava.models.Users;
import com.jztchl.splitwiseclonejava.services.JwtService;
import com.jztchl.splitwiseclonejava.services.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final JwtService jwtService;
    private final ProfileService profileService;

    public ProfileController(JwtService jwtService,ProfileService profileService) {
        this.jwtService = jwtService;
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileDto getProfile() {
        return profileService.getCurrentUser();
    }
}
