package com.itticket.controller;

import com.itticket.entity.AutoAssignRule;
import com.itticket.service.AutoAssignRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auto-assign-rules")
@RequiredArgsConstructor
public class AutoAssignRuleController {

    private final AutoAssignRuleService autoAssignRuleService;

    @GetMapping
    public ResponseEntity<List<AutoAssignRule>> getAllRules() {
        return ResponseEntity.ok(autoAssignRuleService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<AutoAssignRule>> getActiveRules() {
        return ResponseEntity.ok(autoAssignRuleService.findActiveRules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRuleById(@PathVariable Long id) {
        return autoAssignRuleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AutoAssignRule> createRule(@RequestBody AutoAssignRule rule) {
        return ResponseEntity.ok(autoAssignRuleService.create(rule));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AutoAssignRule> updateRule(@PathVariable Long id, @RequestBody AutoAssignRule rule) {
        return ResponseEntity.ok(autoAssignRuleService.update(id, rule));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable Long id) {
        autoAssignRuleService.delete(id);
        return ResponseEntity.ok().build();
    }
}
