package com.itticket.service;

import com.itticket.entity.Sla;
import com.itticket.entity.Ticket;
import com.itticket.entity.Notification;
import com.itticket.entity.User;
import com.itticket.repository.TicketRepository;
import com.itticket.repository.NotificationRepository;
import com.itticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlaMonitoringService {

    private final TicketRepository ticketRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * 每5分钟检查一次SLA超时
     */
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkSlaViolations() {
        log.info("开始检查SLA超时...");

        // 获取所有未关闭的工单
        List<Ticket> activeTickets = ticketRepository.findByStatusNotIn(
                List.of(Ticket.Status.RESOLVED, Ticket.Status.CLOSED)
        );

        LocalDateTime now = LocalDateTime.now();

        for (Ticket ticket : activeTickets) {
            if (ticket.getSla() == null) {
                continue;
            }

            Sla sla = ticket.getSla();
            LocalDateTime createdAt = ticket.getCreatedAt();

            // 计算响应时限和处理时限
            LocalDateTime responseDeadline = createdAt.plusHours(sla.getResponseHours());
            LocalDateTime resolutionDeadline = createdAt.plusHours(sla.getResolutionHours());

            // 检查响应超时
            if (ticket.getStatus() == Ticket.Status.NEW && now.isAfter(responseDeadline)) {
                sendSlaWarning(ticket, "响应", responseDeadline);
            }

            // 检查处理超时
            if (now.isAfter(resolutionDeadline)) {
                sendSlaWarning(ticket, "处理", resolutionDeadline);
            }
        }

        log.info("SLA检查完成");
    }

    private void sendSlaWarning(Ticket ticket, String type, LocalDateTime deadline) {
        String title = String.format("工单 %s SLA %s超时提醒", ticket.getTicketNo(), type);
        String content = String.format(
                "工单【%s】已超过%s时限（截止时间：%s），请尽快处理！\n\n" +
                "工单标题：%s\n" +
                "优先级：%s\n" +
                "当前状态：%s",
                ticket.getTicketNo(), type, deadline,
                ticket.getTitle(),
                ticket.getPriority(),
                ticket.getStatus()
        );

        // 通知处理人
        if (ticket.getAssignee() != null) {
            createNotification(ticket.getAssignee(), title, content, "/tickets/" + ticket.getId());
            emailService.sendEmail(ticket.getAssignee().getEmail(), title, content);
        }

        // 通知报告人
        if (ticket.getReporter() != null) {
            createNotification(ticket.getReporter(), title, content, "/tickets/" + ticket.getId());
        }

        log.warn("SLA超时警告: 工单 {} {}超时", ticket.getTicketNo(), type);
    }

    private void createNotification(User user, String title, String content, String link) {
        // 检查是否已存在相同的未读通知，避免重复发送
        List<Notification> existingNotifications = notificationRepository.findByUserIdAndIsReadFalse(user.getId());
        boolean alreadyNotified = existingNotifications.stream()
                .anyMatch(n -> n.getTitle().equals(title));

        if (!alreadyNotified) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setLink(link);
            notification.setIsRead(false);
            notificationRepository.save(notification);
        }
    }
}
