package com.itticket.dto;

import lombok.Data;

@Data
public class ResponseTimeDto {
    private Long ticketId;
    private String ticketNo;
    private String title;
    private Double responseHours;
    private Double resolutionHours;
}
