package com.iqb.interviewpoc.controller;

import com.iqb.interviewpoc.dto.DashboardStatsDto;
import com.iqb.interviewpoc.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Returns summary stats including totals, averages, top students, recent results, and score distribution")
    public DashboardStatsDto getStats() {
        return service.getStats();
    }
}
