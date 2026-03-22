package com.itticket.service;

import com.itticket.dto.TeamWorkloadDto;
import com.itticket.entity.Ticket;
import com.itticket.entity.User;
import com.itticket.repository.TicketRepository;
import com.itticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
