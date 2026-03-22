package com.itticket.controller;

import com.itticket.dto.AnnouncementDto;
import com.itticket.entity.Announcement;
import com.itticket.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/active")
    public ResponseEntity<List<AnnouncementDto>> getActiveAnnouncements() {
        return ResponseEntity.ok(announcementService.getActiveAnnouncements());
    }

    @GetMapping("/pinned")
    public ResponseEntity<List<AnnouncementDto>> getPinnedAnnouncements() {
        return ResponseEntity.ok(announcementService.getPinnedAnnouncements());
    }

    @GetMapping
    public ResponseEntity<Page<AnnouncementDto>> getAllAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(announcementService.getAllAnnouncements(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AnnouncementDto>> searchAnnouncements(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Announcement.Status statusEnum = status != null ? Announcement.Status.valueOf(status) : null;
        return ResponseEntity.ok(announcementService.searchAnnouncements(statusEnum, keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDto> getAnnouncementById(@PathVariable Long id) {
        AnnouncementDto dto = announcementService.getAnnouncementById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        // Increment view count when viewing detail
        announcementService.incrementViewCount(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<AnnouncementDto> createAnnouncement(
            @RequestBody AnnouncementDto dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Name", required = false) String userName) {
        
        return ResponseEntity.ok(announcementService.createAnnouncement(dto, userId, userName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementDto> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody AnnouncementDto dto) {
        return ResponseEntity.ok(announcementService.updateAnnouncement(id, dto));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<AnnouncementDto> publishAnnouncement(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.publishAnnouncement(id));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<AnnouncementDto> archiveAnnouncement(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.archiveAnnouncement(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok().build();
    }
}
