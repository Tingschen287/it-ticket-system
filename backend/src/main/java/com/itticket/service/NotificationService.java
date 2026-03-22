package com.itticket.service;

import com.itticket.entity.Notification;
import com.itticket.entity.User;
import com.itticket.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(User user, String title, String content, String link, String type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setLink(link);
        notification.setType(type);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }

    public void notifyTicketAssigned(User assignee, String ticketNo, String ticketTitle) {
        createNotification(
                assignee,
                "工单已分配给您",
                "工单 " + ticketNo + " 已分配给您处理：" + ticketTitle,
                "/tickets/" + ticketNo,
                "TICKET_ASSIGNED"
        );
    }

    public void notifyTicketStatusChanged(User user, String ticketNo, String oldStatus, String newStatus) {
        createNotification(
                user,
                "工单状态变更",
                "工单 " + ticketNo + " 状态从 " + oldStatus + " 变更为 " + newStatus,
                "/tickets/" + ticketNo,
                "STATUS_CHANGED"
        );
    }

    public void notifyTicketCommented(User user, String ticketNo, String commenter) {
        createNotification(
                user,
                "工单有新评论",
                commenter + " 在工单 " + ticketNo + " 中发表了评论",
                "/tickets/" + ticketNo,
                "NEW_COMMENT"
        );
    }
}
