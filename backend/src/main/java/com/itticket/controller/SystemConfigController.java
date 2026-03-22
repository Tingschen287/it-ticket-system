package com.itticket.controller;

import com.itticket.dto.SystemConfigDto;
import com.itticket.service.SystemConfigService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService configService;

    @PostConstruct
    public void init() {
        configService.initializeDefaultConfigs();
    }

    @GetMapping
    public ResponseEntity<List<SystemConfigDto>> getAllConfigs() {
        return ResponseEntity.ok(configService.getAllConfigs());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<SystemConfigDto>> getConfigsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(configService.getConfigsByCategory(category));
    }

    @GetMapping("/{key}")
    public ResponseEntity<SystemConfigDto> getConfigByKey(@PathVariable String key) {
        SystemConfigDto config = configService.getConfigByKey(key);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(config);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SystemConfigDto> updateConfig(@PathVariable Long id, @RequestBody SystemConfigDto dto) {
        return ResponseEntity.ok(configService.updateConfig(id, dto));
    }

    @PostMapping
    public ResponseEntity<SystemConfigDto> setConfig(@RequestBody SystemConfigDto dto) {
        return ResponseEntity.ok(configService.setConfig(
                dto.getConfigKey(),
                dto.getConfigValue(),
                dto.getDescription(),
                dto.getCategory()
        ));
    }

    @PostMapping("/batch")
    public ResponseEntity<Void> batchUpdateConfigs(@RequestBody Map<String, String> configs) {
        configService.batchUpdateConfigs(configs);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        configService.deleteConfig(id);
        return ResponseEntity.ok().build();
    }
}
