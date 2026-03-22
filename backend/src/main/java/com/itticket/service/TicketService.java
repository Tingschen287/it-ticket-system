package com.itticket.service;

import com.itticket.dto.CreateTicketRequest;
import com.itticket.dto.TicketDto;
import com.itticket.entity.*;
import com.itticket.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final SlaRepository slaRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final AutoAssignRuleService autoAssignRuleService;

    @Transactional
    public Ticket createTicket(CreateTicketRequest request, User reporter) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setType(request.getType());
        ticket.setPriority(request.getPriority());
        ticket.setReporter(reporter);
        ticket.setStatus(Ticket.Status.NEW);

        if (request.getDepartmentId() != null) {
            departmentRepository.findById(request.getDepartmentId())
                    .ifPresent(ticket::setDepartment);
        }

        // 如果没有指定处理人，尝试自动派发
        if (request.getAssigneeId() != null) {
            userRepository.findById(request.getAssigneeId())
                    .ifPresent(ticket::setAssignee);
        } else {
            // 应用自动派发规则
            autoAssignRuleService.findAssigneeForTicket(ticket)
                    .flatMap(userRepository::findById)
                    .ifPresent(ticket::setAssignee);
        }

        if (request.getSlaId() != null) {
            slaRepository.findById(request.getSlaId())
                    .ifPresent(ticket::setSla);
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        // 记录历史
        saveHistory(savedTicket, "创建工单", null, "NEW", reporter, null);

        return savedTicket;
    }

    @Transactional
    public Ticket updateStatus(Long ticketId, Ticket.Status newStatus, User operator, String comment) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        String oldStatus = ticket.getStatus().name();
        ticket.setStatus(newStatus);
        ticketRepository.save(ticket);

        saveHistory(ticket, "状态变更", oldStatus, newStatus.name(), operator, comment);

        return ticket;
    }

    @Transactional
    public Ticket assignTicket(Long ticketId, Long assigneeId, User operator) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        ticket.setAssignee(assignee);
        ticketRepository.save(ticket);

        saveHistory(ticket, "分配工单", null, assignee.getUsername(), operator, null);

        return ticket;
    }

    public Page<Ticket> searchTickets(Ticket.Status status, Ticket.Priority priority,
                                      Ticket.TicketType type, Long reporterId,
                                      Long assigneeId, Long departmentId, Pageable pageable) {
        return ticketRepository.searchTickets(status, priority, type, reporterId, assigneeId, departmentId, pageable);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
    }

    public Ticket getTicketByTicketNo(String ticketNo) {
        return ticketRepository.findByTicketNo(ticketNo)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
    }

    public List<TicketHistory> getTicketHistory(Long ticketId) {
        return ticketHistoryRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
    }

    public Page<Ticket> getMyTickets(Long userId, Pageable pageable) {
        return ticketRepository.findByReporterId(userId, pageable);
    }

    public Page<Ticket> getAssignedTickets(Long userId, Pageable pageable) {
        return ticketRepository.findByAssigneeId(userId, pageable);
    }

    private void saveHistory(Ticket ticket, String action, String fromStatus, String toStatus, User operator, String comment) {
        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setAction(action);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setOperator(operator);
        history.setComment(comment);
        ticketHistoryRepository.save(history);
    }

    public TicketDto convertToDto(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setTicketNo(ticket.getTicketNo());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setType(ticket.getType());
        dto.setPriority(ticket.getPriority());
        dto.setStatus(ticket.getStatus());
        dto.setReporter(TicketDto.UserSummary.from(ticket.getReporter()));
        dto.setAssignee(TicketDto.UserSummary.from(ticket.getAssignee()));
        dto.setDueDate(ticket.getDueDate());
        dto.setResolvedAt(ticket.getResolvedAt());
        dto.setClosedAt(ticket.getClosedAt());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());

        if (ticket.getDepartment() != null) {
            TicketDto.DepartmentDto deptDto = new TicketDto.DepartmentDto();
            deptDto.setId(ticket.getDepartment().getId());
            deptDto.setName(ticket.getDepartment().getName());
            deptDto.setCode(ticket.getDepartment().getCode());
            dto.setDepartment(deptDto);
        }

        if (ticket.getSla() != null) {
            TicketDto.SlaDto slaDto = new TicketDto.SlaDto();
            slaDto.setId(ticket.getSla().getId());
            slaDto.setName(ticket.getSla().getName());
            dto.setSla(slaDto);
        }

        return dto;
    }
}
