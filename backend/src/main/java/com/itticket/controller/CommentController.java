package com.itticket.controller;

import com.itticket.entity.Comment;
import com.itticket.entity.Ticket;
import com.itticket.entity.User;
import com.itticket.repository.CommentRepository;
import com.itticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;

    @PostMapping("/ticket/{ticketId}")
    public ResponseEntity<?> addComment(
            @PathVariable Long ticketId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setUser(user);
        comment.setContent(request.getContent());

        commentRepository.save(comment);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<Comment>> getTicketComments(@PathVariable Long ticketId) {
        return ResponseEntity.ok(commentRepository.findByTicketIdOrderByCreatedAtDesc(ticketId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        if (!comment.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("无权删除此评论");
        }

        commentRepository.delete(comment);
        return ResponseEntity.ok().build();
    }

    public static class CommentRequest {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
