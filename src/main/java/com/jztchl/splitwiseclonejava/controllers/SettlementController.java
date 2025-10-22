package com.jztchl.splitwiseclonejava.controllers;

import com.jztchl.splitwiseclonejava.dtos.settlement.createSettlementDto;
import com.jztchl.splitwiseclonejava.models.Settlement;
import com.jztchl.splitwiseclonejava.services.SettlementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settlement")
public class SettlementController {
    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }


    @PostMapping("/create")
    public createSettlementDto createSettlement(@Valid @RequestBody createSettlementDto dto) {
        return settlementService.createSettlement(dto);
    }

}
