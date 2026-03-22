package com.itticket.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TicketTrendDto {
    private LocalDate date;
    private Long created;
    private Long resolved;
}
