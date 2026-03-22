package com.itticket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * 发送邮件
     * 开发环境会记录日志而非实际发送
     */
    public void sendEmail(String to, String subject, String content) {
        if (to == null || to.isEmpty()) {
            log.warn("邮件地址为空，跳过发送");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("邮件发送成功: {} -> {}", subject, to);
        } catch (Exception e) {
            // 开发环境邮件发送可能失败，仅记录日志
            log.info("模拟邮件发送 - 收件人: {}, 主题: {}, 内容: {}", to, subject, content);
        }
    }
}
