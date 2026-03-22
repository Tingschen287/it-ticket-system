package com.itticket.service;

import com.itticket.entity.AutoAssignRule;
import com.itticket.entity.Ticket;
import com.itticket.repository.AutoAssignRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutoAssignRuleService {

    private final AutoAssignRuleRepository autoAssignRuleRepository;

    public List<AutoAssignRule> findAll() {
        return autoAssignRuleRepository.findAll();
    }

    public List<AutoAssignRule> findActiveRules() {
        return autoAssignRuleRepository.findByIsActiveTrueOrderByPriorityOrderAsc();
    }

    public Optional<AutoAssignRule> findById(Long id) {
        return autoAssignRuleRepository.findById(id);
    }

    @Transactional
    public AutoAssignRule create(AutoAssignRule rule) {
        return autoAssignRuleRepository.save(rule);
    }

    @Transactional
    public AutoAssignRule update(Long id, AutoAssignRule rule) {
        return autoAssignRuleRepository.findById(id)
                .map(existing -> {
                    existing.setName(rule.getName());
                    existing.setDescription(rule.getDescription());
                    existing.setTicketType(rule.getTicketType());
                    existing.setPriority(rule.getPriority());
                    existing.setDepartmentId(rule.getDepartmentId());
                    existing.setAssigneeId(rule.getAssigneeId());
                    existing.setPriorityOrder(rule.getPriorityOrder());
                    existing.setIsActive(rule.getIsActive());
                    return autoAssignRuleRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("规则不存在"));
    }

    @Transactional
    public void delete(Long id) {
        autoAssignRuleRepository.deleteById(id);
    }

    /**
     * 根据工单信息匹配自动派发规则，返回应分配的用户ID
     */
    public Optional<Long> findAssigneeForTicket(Ticket ticket) {
        List<AutoAssignRule> activeRules = findActiveRules();

        for (AutoAssignRule rule : activeRules) {
            if (matchesRule(ticket, rule) && rule.getAssigneeId() != null) {
                return Optional.of(rule.getAssigneeId());
            }
        }

        return Optional.empty();
    }

    private boolean matchesRule(Ticket ticket, AutoAssignRule rule) {
        // 检查工单类型
        if (rule.getTicketType() != null && rule.getTicketType() != ticket.getType()) {
            return false;
        }

        // 检查优先级
        if (rule.getPriority() != null && rule.getPriority() != ticket.getPriority()) {
            return false;
        }

        // 检查部门
        if (rule.getDepartmentId() != null) {
            if (ticket.getDepartment() == null || !rule.getDepartmentId().equals(ticket.getDepartment().getId())) {
                return false;
            }
        }

        return true;
    }
}
