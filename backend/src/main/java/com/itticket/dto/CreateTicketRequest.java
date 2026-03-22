package com.itticket.dto;

import com.itticket.entity.Ticket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTicketRequest {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Ticket.TicketType type;

    @NotNull
    private Ticket.Priority priority;

    private Long departmentId;

    private Long assigneeId;

    private Long slaId;
}
