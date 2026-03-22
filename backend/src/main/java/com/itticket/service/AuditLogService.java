package com.itticket.service;

import com.itticket.dto.AuditLogDto;
import com.itticket.entity.AuditLog;
import com.itticket.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(Long userId, String username, String action, String entityType, 
                    Long entityId, String description, HttpServletRequest request) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        
        if (request != null) {
            log.setIpAddress(getClientIp(request));
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setRequestUrl(request.getRequestURL().toString());
            log.setRequestMethod(request.getMethod());
        }
        
        auditLogRepository.save(log);
    }

    @Transactional
    public void log(Long userId, String username, String action, String entityType, 
                    Long entityId, String description) {
        log(userId, username, action, entityType, entityId, description, null);
    }

    public Page<AuditLogDto> searchLogs(Long userId, String action, String entityType,
                                         LocalDateTime startTime, LocalDateTime endTime,
                                         Pageable pageable) {
        return auditLogRepository.searchLogs(userId, action, entityType, startTime, endTime, pageable)
                .map(this::toDto);
    }

    public Page<AuditLogDto> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable)
                .map(this::toDto);
    }

    public List<AuditLogDto> getEntityLogs(Long entityId) {
        return auditLogRepository.findByEntityIdOrderByCreatedAtDesc(entityId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<AuditLogDto> getUserLogs(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable)
                .map(this::toDto);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Handle multiple IPs in X-Forwarded-For
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private AuditLogDto toDto(AuditLog log) {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(log.getId());
        dto.setUserId(log.getUserId());
        dto.setUsername(log.getUsername());
        dto.setAction(log.getAction());
        dto.setEntityType(log.getEntityType());
        dto.setEntityId(log.getEntityId());
        dto.setDescription(log.getDescription());
        dto.setIpAddress(log.getIpAddress());
        dto.setRequestUrl(log.getRequestUrl());
        dto.setRequestMethod(log.getRequestMethod());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }
}
