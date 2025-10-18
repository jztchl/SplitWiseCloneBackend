package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.ProfileDto;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final JwtService jwtService;

    public ProfileService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public ProfileDto getCurrentUser() {
        ProfileDto profile = new ProfileDto();
        profile.setName(jwtService.getCurrentUser().getName());
        profile.setEmail(jwtService.getCurrentUser().getEmail());
        profile.setProfilePictureUrl(jwtService.getCurrentUser().getProfilePictureUrl());
        profile.setCreatedAt(String.valueOf(jwtService.getCurrentUser().getCreatedAt()));
        profile.setExposure(200.67D); // need to implement exposure calculation
        profile.setLiability(100.50D);// need to implement liability calculation

        return profile;
    }

    // private Double calculateExposure(){}
    // private Double calculateLiablity(){}
}
