package com.itticket.dto;

import lombok.Data;

@Data
public class TeamWorkloadDto {
    private Long userId;
    private String username;
    private String fullName;
    private String department;
    private Long totalTickets;
    private Long openTickets;
    private Long resolvedTickets;
    private Double avgResolutionHours;
}
