package com.itticket.dto;

import lombok.Data;

@Data
public class SystemConfigDto {
    private Long id;
    private String configKey;
    private String configValue;
    private String description;
    private String category;
}
