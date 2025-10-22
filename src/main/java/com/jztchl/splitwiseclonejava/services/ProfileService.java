package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.ProfileDto;
import com.jztchl.splitwiseclonejava.models.ExpenseShare;
import com.jztchl.splitwiseclonejava.models.Users;
import com.jztchl.splitwiseclonejava.repos.ExpenseShareRepository;
import com.jztchl.splitwiseclonejava.utility.MiscCalculations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileService {
    private final JwtService jwtService;
    private final MiscCalculations miscCalculations;
    private final ExpenseShareRepository expenseShareRepository;

    public ProfileService(JwtService jwtService, MiscCalculations miscCalculations
            , ExpenseShareRepository expenseShareRepository) {
        this.jwtService = jwtService;
        this.miscCalculations = miscCalculations;
        this.expenseShareRepository = expenseShareRepository;
    }

    public ProfileDto getCurrentUser() {
        ProfileDto profile = new ProfileDto();
        profile.setId(Long.valueOf(jwtService.getCurrentUser().getId()));
        profile.setName(jwtService.getCurrentUser().getName());
        profile.setEmail(jwtService.getCurrentUser().getEmail());
        profile.setProfilePictureUrl(jwtService.getCurrentUser().getProfilePictureUrl());
        profile.setCreatedAt(String.valueOf(jwtService.getCurrentUser().getCreatedAt()));
        profile.setLiability(calculateLiablity(jwtService.getCurrentUser()));


        return profile;
    }


    private BigDecimal calculateLiablity(Users user) {
        BigDecimal totalLiability = BigDecimal.valueOf(0);
        List<ExpenseShare> shares = new ArrayList<>();
        shares = expenseShareRepository.findAllByUserId(user);
        for (ExpenseShare share : shares) {
            if (share.isPaid()) {
                continue;
            }
            BigDecimal remaing = share.getAmountOwed().subtract(miscCalculations.calculateAmountTillNow(share));
            totalLiability = totalLiability.add(remaing);
        }
        return totalLiability;
    }
}
