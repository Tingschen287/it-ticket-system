package com.itticket.dto;

import com.itticket.entity.Ticket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketDto {
    private Long id;
    private String ticketNo;
    private String title;
    private String description;
    private Ticket.TicketType type;
    private Ticket.Priority priority;
    private Ticket.Status status;
    private UserSummary reporter;
    private UserSummary assignee;
    private DepartmentDto department;
    private SlaDto sla;
    private LocalDateTime dueDate;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class UserSummary {
        private Long id;
        private String username;
        private String fullName;

        public static UserSummary from(com.itticket.entity.User user) {
            if (user == null) return null;
            UserSummary summary = new UserSummary();
            summary.setId(user.getId());
            summary.setUsername(user.getUsername());
            summary.setFullName(user.getFullName());
            return summary;
        }
    }

    @Data
    public static class DepartmentDto {
        private Long id;
        private String name;
        private String code;
    }

    @Data
    public static class SlaDto {
        private Long id;
        private String name;
    }
}
