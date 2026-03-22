package com.itticket.controller;

import com.itticket.dto.ResponseTimeDto;
import com.itticket.dto.TeamWorkloadDto;
import com.itticket.dto.TicketTrendDto;
import com.itticket.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/team-workload")
    public ResponseEntity<List<TeamWorkloadDto>> getTeamWorkload(
            @RequestParam(required = false) Long departmentId) {
        return ResponseEntity.ok(statisticsService.getTeamWorkload(departmentId));
    }

    @GetMapping("/by-status")
    public ResponseEntity<Map<String, Long>> getTicketCountByStatus() {
        return ResponseEntity.ok(statisticsService.getTicketCountByStatus());
    }

    @GetMapping("/by-priority")
    public ResponseEntity<Map<String, Long>> getTicketCountByPriority() {
        return ResponseEntity.ok(statisticsService.getTicketCountByPriority());
    }

    @GetMapping("/by-type")
    public ResponseEntity<Map<String, Long>> getTicketCountByType() {
        return ResponseEntity.ok(statisticsService.getTicketCountByType());
    }

    @GetMapping("/total")
    public ResponseEntity<Long> getTotalTicketCount() {
        return ResponseEntity.ok(statisticsService.getTotalTicketCount());
    }

    @GetMapping("/open")
    public ResponseEntity<Long> getOpenTicketCount() {
        return ResponseEntity.ok(statisticsService.getOpenTicketCount());
    }

    @GetMapping("/trend")
    public ResponseEntity<List<TicketTrendDto>> getTicketTrend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getTicketTrend(startDate, endDate));
    }

    @GetMapping("/response-time")
    public ResponseEntity<List<ResponseTimeDto>> getResponseTimeStats(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        return ResponseEntity.ok(statisticsService.getResponseTimeStats(departmentId, limit));
    }

    @GetMapping("/avg-response-time")
    public ResponseEntity<Map<String, Double>> getAverageResponseTimes() {
        return ResponseEntity.ok(statisticsService.getAverageResponseTimes());
    }
}
