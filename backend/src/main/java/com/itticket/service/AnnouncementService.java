package com.itticket.service;

import com.itticket.dto.AnnouncementDto;
import com.itticket.entity.Announcement;
import com.itticket.repository.AnnouncementRepository;
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
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public List<AnnouncementDto> getActiveAnnouncements() {
        return announcementRepository.findActiveAnnouncements(Announcement.Status.PUBLISHED, LocalDateTime.now())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AnnouncementDto> getPinnedAnnouncements() {
        return announcementRepository.findByStatusAndIsPinnedTrueOrderByCreatedAtDesc(Announcement.Status.PUBLISHED)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<AnnouncementDto> searchAnnouncements(Announcement.Status status, String keyword, Pageable pageable) {
        return announcementRepository.searchAnnouncements(status, keyword, pageable)
                .map(this::toDto);
    }

    public Page<AnnouncementDto> getAllAnnouncements(Pageable pageable) {
        return announcementRepository.findAll(pageable)
                .map(this::toDto);
    }

    public AnnouncementDto getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional
    public AnnouncementDto createAnnouncement(AnnouncementDto dto, Long authorId, String authorName) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setAuthorId(authorId);
        announcement.setAuthorName(authorName);
        
        if (dto.getPriority() != null) {
            announcement.setPriority(Announcement.Priority.valueOf(dto.getPriority()));
        }
        if (dto.getStatus() != null) {
            announcement.setStatus(Announcement.Status.valueOf(dto.getStatus()));
        }
        announcement.setStartTime(dto.getStartTime());
        announcement.setEndTime(dto.getEndTime());
        announcement.setIsPinned(dto.getIsPinned() != null ? dto.getIsPinned() : false);
        announcement.setViewCount(0);
        
        return toDto(announcementRepository.save(announcement));
    }

    @Transactional
    public AnnouncementDto updateAnnouncement(Long id, AnnouncementDto dto) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));

        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        
        if (dto.getPriority() != null) {
            announcement.setPriority(Announcement.Priority.valueOf(dto.getPriority()));
        }
        if (dto.getStatus() != null) {
            announcement.setStatus(Announcement.Status.valueOf(dto.getStatus()));
        }
        announcement.setStartTime(dto.getStartTime());
        announcement.setEndTime(dto.getEndTime());
        announcement.setIsPinned(dto.getIsPinned());

        return toDto(announcementRepository.save(announcement));
    }

    @Transactional
    public AnnouncementDto publishAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));
        announcement.setStatus(Announcement.Status.PUBLISHED);
        return toDto(announcementRepository.save(announcement));
    }

    @Transactional
    public AnnouncementDto archiveAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));
        announcement.setStatus(Announcement.Status.ARCHIVED);
        return toDto(announcementRepository.save(announcement));
    }

    @Transactional
    public void incrementViewCount(Long id) {
        announcementRepository.findById(id).ifPresent(announcement -> {
            announcement.setViewCount(announcement.getViewCount() + 1);
            announcementRepository.save(announcement);
        });
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    private AnnouncementDto toDto(Announcement announcement) {
        AnnouncementDto dto = new AnnouncementDto();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setAuthorId(announcement.getAuthorId());
        dto.setAuthorName(announcement.getAuthorName());
        dto.setPriority(announcement.getPriority() != null ? announcement.getPriority().name() : null);
        dto.setStatus(announcement.getStatus() != null ? announcement.getStatus().name() : null);
        dto.setStartTime(announcement.getStartTime());
        dto.setEndTime(announcement.getEndTime());
        dto.setIsPinned(announcement.getIsPinned());
        dto.setViewCount(announcement.getViewCount());
        dto.setCreatedAt(announcement.getCreatedAt());
        dto.setUpdatedAt(announcement.getUpdatedAt());
        return dto;
    }
}
