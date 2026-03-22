package com.itticket.repository;

import com.itticket.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    Page<Announcement> findByStatus(Announcement.Status status, Pageable pageable);
    
    List<Announcement> findByStatusAndIsPinnedTrueOrderByCreatedAtDesc(Announcement.Status status);
    
    @Query("SELECT a FROM Announcement a WHERE a.status = :status " +
           "AND (:now >= a.startTime OR a.startTime IS NULL) " +
           "AND (:now <= a.endTime OR a.endTime IS NULL) " +
           "ORDER BY a.isPinned DESC, a.createdAt DESC")
    List<Announcement> findActiveAnnouncements(@Param("status") Announcement.Status status, 
                                                @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:keyword IS NULL OR a.title LIKE %:keyword% OR a.content LIKE %:keyword%)")
    Page<Announcement> searchAnnouncements(@Param("status") Announcement.Status status,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);
}
