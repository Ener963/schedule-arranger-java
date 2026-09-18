package com.example.schedule_arranger.controller;

import com.example.schedule_arranger.entity.Comment;
import com.example.schedule_arranger.entity.CommentId;
import com.example.schedule_arranger.repository.CommentRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/schedules")
public class CommentController {

    private static final int COMMENT_MAX_LENGTH = 255;

    private final CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @PostMapping("/{scheduleId}/users/{userId}/comments")
    public Map<String, Object> update(@PathVariable UUID scheduleId,
                                      @PathVariable Integer userId,
                                      @RequestBody CommentRequest body) {
        String comment = truncate(body.comment(), COMMENT_MAX_LENGTH);

        Comment entity = commentRepository.findById(new CommentId(scheduleId, userId))
                .orElseGet(Comment::new);
        entity.setScheduleId(scheduleId);
        entity.setUserId(userId);
        entity.setComment(comment);
        commentRepository.save(entity);

        return Map.of("status", "OK", "comment", comment);
    }

    private String truncate(String value, int maxLength) {
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    public record CommentRequest(String comment) {
    }
}