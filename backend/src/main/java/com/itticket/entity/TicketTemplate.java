package com.itticket.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String titlePrefix;

    @Column(columnDefinition = "TEXT")
    private String defaultDescription;

    @Enumerated(EnumType.STRING)
    private Ticket.TicketType defaultType;

    @Enumerated(EnumType.STRING)
    private Ticket.Priority defaultPriority;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
