package com.itticket.controller;

import com.itticket.dto.CreateTicketRequest;
import com.itticket.dto.TicketDto;
import com.itticket.entity.Ticket;
import com.itticket.entity.TicketHistory;
import com.itticket.entity.User;
import com.itticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<?> createTicket(@Valid @RequestBody CreateTicketRequest request,
                                          @AuthenticationPrincipal User user) {
        Ticket ticket = ticketService.createTicket(request, user);
        return ResponseEntity.ok(ticketService.convertToDto(ticket));
    }

    @GetMapping
    public ResponseEntity<?> getTickets(
            @RequestParam(required = false) Ticket.Status status,
            @RequestParam(required = false) Ticket.Priority priority,
            @RequestParam(required = false) Ticket.TicketType type,
            @RequestParam(required = false) Long reporterId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Ticket> tickets = ticketService.searchTickets(status, priority, type, reporterId, assigneeId, departmentId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tickets.getContent().stream()
                .map(ticketService::convertToDto)
                .collect(Collectors.toList()));
        response.put("totalElements", tickets.getTotalElements());
        response.put("totalPages", tickets.getTotalPages());
        response.put("page", tickets.getNumber());
        response.put("size", tickets.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        try {
            Ticket ticket = ticketService.getTicketById(id);
            return ResponseEntity.ok(ticketService.convertToDto(ticket));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/no/{ticketNo}")
    public ResponseEntity<?> getTicketByTicketNo(@PathVariable String ticketNo) {
        try {
            Ticket ticket = ticketService.getTicketByTicketNo(ticketNo);
            return ResponseEntity.ok(ticketService.convertToDto(ticket));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam Ticket.Status status,
                                          @RequestParam(required = false) String comment,
                                          @AuthenticationPrincipal User user) {
        try {
            Ticket ticket = ticketService.updateStatus(id, status, user, comment);
            return ResponseEntity.ok(ticketService.convertToDto(ticket));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignTicket(@PathVariable Long id,
                                          @RequestParam Long assigneeId,
                                          @AuthenticationPrincipal User user) {
        try {
            Ticket ticket = ticketService.assignTicket(id, assigneeId, user);
            return ResponseEntity.ok(ticketService.convertToDto(ticket));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getTicketHistory(@PathVariable Long id) {
        List<TicketHistory> history = ticketService.getTicketHistory(id);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyTickets(@AuthenticationPrincipal User user,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Ticket> tickets = ticketService.getMyTickets(user.getId(), pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tickets.getContent().stream()
                .map(ticketService::convertToDto)
                .collect(Collectors.toList()));
        response.put("totalElements", tickets.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/assigned")
    public ResponseEntity<?> getAssignedTickets(@AuthenticationPrincipal User user,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Ticket> tickets = ticketService.getAssignedTickets(user.getId(), pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tickets.getContent().stream()
                .map(ticketService::convertToDto)
                .collect(Collectors.toList()));
        response.put("totalElements", tickets.getTotalElements());

        return ResponseEntity.ok(response);
    }
}
