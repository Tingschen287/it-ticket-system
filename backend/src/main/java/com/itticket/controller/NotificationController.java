package com.itticket.controller;

import com.itticket.entity.Notification;
import com.itticket.entity.User;
import com.itticket.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<?> getNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("content", notifications.getContent());
        response.put("totalElements", notifications.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationRepository.findByUserIdAndIsReadFalse(user.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        Map<String, Long> response = new HashMap<>();
        response.put("count", notificationRepository.countUnreadByUserId(user.getId()));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("通知不存在"));

        if (!notification.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("无权操作");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationRepository.markAllAsReadByUserId(user.getId());
        return ResponseEntity.ok().build();
    }
}
