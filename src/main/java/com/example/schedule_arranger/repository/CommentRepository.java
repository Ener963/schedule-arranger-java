package com.example.schedule_arranger.repository;

import com.example.schedule_arranger.entity.Comment;
import com.example.schedule_arranger.entity.CommentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, CommentId> {

    List<Comment> findByScheduleId(UUID scheduleId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.scheduleId = :scheduleId")
    void deleteByScheduleId(@Param("scheduleId") UUID scheduleId);
}
