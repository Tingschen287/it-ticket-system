package com.itticket.repository;

import com.itticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNo(String ticketNo);

    Page<Ticket> findByReporterId(Long reporterId, Pageable pageable);

    Page<Ticket> findByAssigneeId(Long assigneeId, Pageable pageable);

    Page<Ticket> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Ticket> findByStatus(Ticket.Status status, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:reporterId IS NULL OR t.reporter.id = :reporterId) AND " +
           "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND " +
           "(:departmentId IS NULL OR t.department.id = :departmentId)")
    Page<Ticket> searchTickets(
            @Param("status") Ticket.Status status,
            @Param("priority") Ticket.Priority priority,
            @Param("type") Ticket.TicketType type,
            @Param("reporterId") Long reporterId,
            @Param("assigneeId") Long assigneeId,
            @Param("departmentId") Long departmentId,
            Pageable pageable);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    long countByStatus(@Param("status") Ticket.Status status);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignee.id = :assigneeId")
    long countByAssigneeId(@Param("assigneeId") Long assigneeId);

    List<Ticket> findByStatusNotIn(List<Ticket.Status> statuses);
}
