package com.itticket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auto_assign_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoAssignRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type")
    private Ticket.TicketType ticketType;

    @Enumerated(EnumType.STRING)
    private Ticket.Priority priority;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "assignee_id")
    private Long assigneeId;

    @Column(name = "priority_order")
    private Integer priorityOrder;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (priorityOrder == null) {
            priorityOrder = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
