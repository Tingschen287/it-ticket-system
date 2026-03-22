package com.itticket.controller;

import com.itticket.entity.SystemConfig;
import com.itticket.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configs")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigRepository systemConfigRepository;

    @GetMapping
    public ResponseEntity<List<SystemConfig>> getAllConfigs() {
        return ResponseEntity.ok(systemConfigRepository.findAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getConfigByKey(@PathVariable String key) {
        return systemConfigRepository.findByConfigKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createConfig(@RequestBody SystemConfig config) {
        if (systemConfigRepository.findByConfigKey(config.getConfigKey()).isPresent()) {
            return ResponseEntity.badRequest().body("配置键已存在");
        }
        return ResponseEntity.ok(systemConfigRepository.save(config));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateConfig(@PathVariable Long id, @RequestBody SystemConfig config) {
        return systemConfigRepository.findById(id)
                .map(existing -> {
                    existing.setConfigValue(config.getConfigValue());
                    existing.setDescription(config.getDescription());
                    return ResponseEntity.ok(systemConfigRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConfig(@PathVariable Long id) {
        if (!systemConfigRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        systemConfigRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
