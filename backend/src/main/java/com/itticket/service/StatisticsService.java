package com.itticket.service;

import com.itticket.dto.ResponseTimeDto;
import com.itticket.dto.TeamWorkloadDto;
import com.itticket.dto.TicketTrendDto;
import com.itticket.entity.Ticket;
import com.itticket.entity.User;
import com.itticket.repository.TicketRepository;
import com.itticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public List<TeamWorkloadDto> getTeamWorkload(Long departmentId) {
        List<User> users;

        if (departmentId != null) {
            users = userRepository.findByDepartmentId(departmentId);
        } else {
            users = userRepository.findAll();
        }

        List<TeamWorkloadDto> result = new ArrayList<>();

        for (User user : users) {
            TeamWorkloadDto dto = new TeamWorkloadDto();
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setFullName(user.getFullName());
            dto.setDepartment(user.getDepartment() != null ? user.getDepartment().getName() : null);

            // 获取该用户的工单统计
            List<Ticket> assignedTickets = ticketRepository.findAll().stream()
                    .filter(t -> t.getAssignee() != null && t.getAssignee().getId().equals(user.getId()))
                    .collect(Collectors.toList());

            dto.setTotalTickets((long) assignedTickets.size());
            dto.setOpenTickets(assignedTickets.stream()
                    .filter(t -> t.getStatus() != Ticket.Status.RESOLVED && t.getStatus() != Ticket.Status.CLOSED)
                    .count());
            dto.setResolvedTickets(assignedTickets.stream()
                    .filter(t -> t.getStatus() == Ticket.Status.RESOLVED || t.getStatus() == Ticket.Status.CLOSED)
                    .count());

            // 计算平均解决时间
            double avgHours = assignedTickets.stream()
                    .filter(t -> t.getResolvedAt() != null && t.getCreatedAt() != null)
                    .mapToLong(t -> ChronoUnit.HOURS.between(t.getCreatedAt(), t.getResolvedAt()))
                    .average()
                    .orElse(0.0);
            dto.setAvgResolutionHours(avgHours);

            result.add(dto);
        }

        return result;
    }

    public Map<String, Long> getTicketCountByStatus() {
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets.stream()
                .collect(Collectors.groupingBy(t -> t.getStatus().name(), Collectors.counting()));
    }

    public Map<String, Long> getTicketCountByPriority() {
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets.stream()
                .collect(Collectors.groupingBy(t -> t.getPriority().name(), Collectors.counting()));
    }

    public Map<String, Long> getTicketCountByType() {
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets.stream()
                .collect(Collectors.groupingBy(t -> t.getType().name(), Collectors.counting()));
    }

    public Long getTotalTicketCount() {
        return ticketRepository.count();
    }

    public Long getOpenTicketCount() {
        return ticketRepository.findAll().stream()
                .filter(t -> t.getStatus() != Ticket.Status.RESOLVED && t.getStatus() != Ticket.Status.CLOSED)
                .count();
    }

    /**
     * 获取工单趋势分析 - 按天统计创建和解决的工单数量
     */
    public List<TicketTrendDto> getTicketTrend(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<TicketTrendDto> trends = new ArrayList<>();
        List<Ticket> allTickets = ticketRepository.findAll();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            final LocalDate date = current;

            // 统计当天创建的工单
            long createdCount = allTickets.stream()
                    .filter(t -> t.getCreatedAt() != null)
                    .filter(t -> t.getCreatedAt().toLocalDate().equals(date))
                    .count();

            // 统计当天解决的工单
            long resolvedCount = allTickets.stream()
                    .filter(t -> t.getResolvedAt() != null)
                    .filter(t -> t.getResolvedAt().toLocalDate().equals(date))
                    .count();

            TicketTrendDto dto = new TicketTrendDto();
            dto.setDate(date);
            dto.setCreated(createdCount);
            dto.setResolved(resolvedCount);
            trends.add(dto);

            current = current.plusDays(1);
        }

        return trends;
    }

    /**
     * 获取响应时间统计 - 统计工单的响应时间和解决时间
     */
    public List<ResponseTimeDto> getResponseTimeStats(Long departmentId, Integer limit) {
        List<Ticket> tickets;

        if (departmentId != null) {
            tickets = ticketRepository.findAll().stream()
                    .filter(t -> t.getDepartment() != null && t.getDepartment().getId().equals(departmentId))
                    .collect(Collectors.toList());
        } else {
            tickets = ticketRepository.findAll();
        }

        // 只统计已解决或已关闭的工单
        List<Ticket> completedTickets = tickets.stream()
                .filter(t -> t.getStatus() == Ticket.Status.RESOLVED || t.getStatus() == Ticket.Status.CLOSED)
                .collect(Collectors.toList());

        List<ResponseTimeDto> result = completedTickets.stream()
                .map(ticket -> {
                    ResponseTimeDto dto = new ResponseTimeDto();
                    dto.setTicketId(ticket.getId());
                    dto.setTicketNo(ticket.getTicketNo());
                    dto.setTitle(ticket.getTitle());

                    // 计算响应时间（从创建到首次分配或状态变更）
                    // 简化计算：使用创建时间到更新时间的差值作为响应时间
                    if (ticket.getUpdatedAt() != null && ticket.getCreatedAt() != null) {
                        double responseHours = ChronoUnit.MINUTES.between(
                                ticket.getCreatedAt(), ticket.getUpdatedAt()) / 60.0;
                        dto.setResponseHours(Math.max(0, responseHours));
                    }

                    // 计算解决时间
                    if (ticket.getResolvedAt() != null && ticket.getCreatedAt() != null) {
                        double resolutionHours = ChronoUnit.MINUTES.between(
                                ticket.getCreatedAt(), ticket.getResolvedAt()) / 60.0;
                        dto.setResolutionHours(Math.max(0, resolutionHours));
                    }

                    return dto;
                })
                .sorted((a, b) -> Double.compare(
                        b.getResolutionHours() != null ? b.getResolutionHours() : 0,
                        a.getResolutionHours() != null ? a.getResolutionHours() : 0))
                .collect(Collectors.toList());

        if (limit != null && limit > 0 && result.size() > limit) {
            return result.subList(0, limit);
        }

        return result;
    }

    /**
     * 获取平均响应时间
     */
    public Map<String, Double> getAverageResponseTimes() {
        List<Ticket> allTickets = ticketRepository.findAll();

        List<Ticket> completedTickets = allTickets.stream()
                .filter(t -> t.getStatus() == Ticket.Status.RESOLVED || t.getStatus() == Ticket.Status.CLOSED)
                .filter(t -> t.getResolvedAt() != null && t.getCreatedAt() != null)
                .collect(Collectors.toList());

        double avgResolutionHours = completedTickets.stream()
                .mapToLong(t -> ChronoUnit.MINUTES.between(t.getCreatedAt(), t.getResolvedAt()))
                .average()
                .orElse(0.0) / 60.0;

        return Map.of("avgResolutionHours", avgResolutionHours);
    }
}
