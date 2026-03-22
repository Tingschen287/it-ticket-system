package com.itticket.controller;

import com.itticket.entity.NotificationPreference;
import com.itticket.entity.User;
import com.itticket.service.NotificationPreferenceService;
import com.itticket.service.AuthService;
import com.itticket.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<NotificationPreference> getMyPreferences(
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtils.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = authService.getCurrentUser(username);
        return ResponseEntity.ok(preferenceService.getOrCreatePreference(user.getId()));
    }

    @PutMapping
    public ResponseEntity<NotificationPreference> updateMyPreferences(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody NotificationPreference preference) {
        String username = jwtUtils.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = authService.getCurrentUser(username);
        return ResponseEntity.ok(preferenceService.updatePreference(user.getId(), preference));
    }
}
