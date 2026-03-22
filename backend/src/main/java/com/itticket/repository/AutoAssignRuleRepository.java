package com.itticket.repository;

import com.itticket.entity.AutoAssignRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoAssignRuleRepository extends JpaRepository<AutoAssignRule, Long> {

    List<AutoAssignRule> findByIsActiveTrueOrderByPriorityOrderAsc();

    List<AutoAssignRule> findByTicketType(com.itticket.entity.Ticket.TicketType ticketType);

    List<AutoAssignRule> findByPriority(com.itticket.entity.Ticket.Priority priority);

    List<AutoAssignRule> findByDepartmentId(Long departmentId);
}
