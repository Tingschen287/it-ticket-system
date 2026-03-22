package com.itticket.controller;

import com.itticket.entity.Attachment;
import com.itticket.entity.Ticket;
import com.itticket.entity.User;
import com.itticket.repository.AttachmentRepository;
import com.itticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload/{ticketId}")
    public ResponseEntity<?> uploadFile(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) throws IOException {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String newFilename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = uploadPath.resolve(newFilename);
        file.transferTo(filePath.toFile());

        // 保存附件记录
        Attachment attachment = new Attachment();
        attachment.setTicket(ticket);
        attachment.setFileName(originalFilename);
        attachment.setFilePath(filePath.toString());
        attachment.setFileSize(file.getSize());
        attachment.setContentType(file.getContentType());
        attachment.setUploader(user);

        attachmentRepository.save(attachment);

        return ResponseEntity.ok(attachment);
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<Attachment>> getTicketAttachments(@PathVariable Long ticketId) {
        return ResponseEntity.ok(attachmentRepository.findByTicketId(ticketId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttachment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) throws IOException {

        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("附件不存在"));

        // 删除文件
        File file = new File(attachment.getFilePath());
        if (file.exists()) {
            file.delete();
        }

        attachmentRepository.delete(attachment);
        return ResponseEntity.ok().build();
    }
}
