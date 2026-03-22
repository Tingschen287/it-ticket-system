package com.itticket.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogDto {
    private Long id;
    private Long userId;
    private String username;
    private String action;
    private String entityType;
    private Long entityId;
    private String description;
    private String ipAddress;
    private String requestUrl;
    private String requestMethod;
    private LocalDateTime createdAt;
}
