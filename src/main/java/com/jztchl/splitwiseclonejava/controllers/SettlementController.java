package com.jztchl.splitwiseclonejava.controllers;

import com.jztchl.splitwiseclonejava.dtos.settlement.CreateSettlementDto;
import com.jztchl.splitwiseclonejava.dtos.settlement.ListSettlementDto;
import com.jztchl.splitwiseclonejava.dtos.settlement.MarkSettlementDto;
import com.jztchl.splitwiseclonejava.dtos.settlement.SettlementDetailDto;
import com.jztchl.splitwiseclonejava.services.SettlementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlement")
public class SettlementController {
    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }


    @PostMapping("/create")
    public CreateSettlementDto createSettlement(@Valid @RequestBody CreateSettlementDto dto) {
        return settlementService.createSettlement(dto);
    }

    @PostMapping("/mark-settlment-status")
    public String markSettlemntStatus(@Valid @RequestBody MarkSettlementDto dto) {
        return settlementService.markSettlmentStatus(dto);

    }

    @GetMapping("/list-settlement/{id}")
    public List<ListSettlementDto> listSettlement(@PathVariable Long id) {
        return settlementService.listSettlements(id);
    }

    @GetMapping("/{id}")
    public SettlementDetailDto getSettlement(@PathVariable Long id) {
        return settlementService.getSettlement(id);
    }

}
