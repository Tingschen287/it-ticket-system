package com.itticket.service;

import com.itticket.dto.SystemConfigDto;
import com.itticket.entity.SystemConfig;
import com.itticket.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository configRepository;

    public List<SystemConfigDto> getAllConfigs() {
        return configRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SystemConfigDto> getConfigsByCategory(String category) {
        return configRepository.findByCategory(category).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SystemConfigDto getConfigByKey(String key) {
        return configRepository.findByConfigKey(key)
                .map(this::toDto)
                .orElse(null);
    }

    public String getConfigValue(String key) {
        return configRepository.findByConfigKey(key)
                .map(SystemConfig::getConfigValue)
                .orElse(null);
    }

    public String getConfigValue(String key, String defaultValue) {
        String value = getConfigValue(key);
        return value != null ? value : defaultValue;
    }

    @Transactional
    public SystemConfigDto setConfig(String key, String value, String description, String category) {
        SystemConfig config = configRepository.findByConfigKey(key)
                .orElseGet(() -> {
                    SystemConfig newConfig = new SystemConfig();
                    newConfig.setConfigKey(key);
                    return newConfig;
                });

        config.setConfigValue(value);
        if (description != null) {
            config.setDescription(description);
        }
        if (category != null) {
            config.setCategory(category);
        }

        return toDto(configRepository.save(config));
    }

    @Transactional
    public SystemConfigDto updateConfig(Long id, SystemConfigDto dto) {
        SystemConfig config = configRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Config not found with id: " + id));

        config.setConfigValue(dto.getConfigValue());
        if (dto.getDescription() != null) {
            config.setDescription(dto.getDescription());
        }
        if (dto.getCategory() != null) {
            config.setCategory(dto.getCategory());
        }

        return toDto(configRepository.save(config));
    }

    @Transactional
    public void deleteConfig(Long id) {
        configRepository.deleteById(id);
    }

    @Transactional
    public void batchUpdateConfigs(Map<String, String> configs) {
        configs.forEach((key, value) -> {
            configRepository.findByConfigKey(key).ifPresentOrElse(
                    config -> {
                        config.setConfigValue(value);
                        configRepository.save(config);
                    },
                    () -> {
                        SystemConfig newConfig = new SystemConfig();
                        newConfig.setConfigKey(key);
                        newConfig.setConfigValue(value);
                        configRepository.save(newConfig);
                    }
            );
        });
    }

    public void initializeDefaultConfigs() {
        if (!configRepository.existsByConfigKey("system.name")) {
            setConfig("system.name", "IT工单管理系统", "系统名称", "general");
        }
        if (!configRepository.existsByConfigKey("system.logo")) {
            setConfig("system.logo", "", "系统Logo URL", "general");
        }
        if (!configRepository.existsByConfigKey("ticket.prefix.bug")) {
            setConfig("ticket.prefix.bug", "BUG", "Bug工单前缀", "ticket");
        }
        if (!configRepository.existsByConfigKey("ticket.prefix.feature")) {
            setConfig("ticket.prefix.feature", "FTR", "功能工单前缀", "ticket");
        }
        if (!configRepository.existsByConfigKey("ticket.prefix.task")) {
            setConfig("ticket.prefix.task", "TSK", "任务工单前缀", "ticket");
        }
        if (!configRepository.existsByConfigKey("notification.email.enabled")) {
            setConfig("notification.email.enabled", "false", "是否启用邮件通知", "notification");
        }
        if (!configRepository.existsByConfigKey("sla.warning.hours")) {
            setConfig("sla.warning.hours", "4", "SLA预警提前小时数", "sla");
        }
    }

    private SystemConfigDto toDto(SystemConfig config) {
        SystemConfigDto dto = new SystemConfigDto();
        dto.setId(config.getId());
        dto.setConfigKey(config.getConfigKey());
        dto.setConfigValue(config.getConfigValue());
        dto.setDescription(config.getDescription());
        dto.setCategory(config.getCategory());
        return dto;
    }
}
