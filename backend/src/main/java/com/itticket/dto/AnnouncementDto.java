package com.itticket.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnnouncementDto {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private String priority;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isPinned;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
