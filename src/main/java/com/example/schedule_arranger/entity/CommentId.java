package com.example.schedule_arranger.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CommentId implements Serializable {

    private UUID scheduleId;
    private Integer userId;

    public CommentId() {
    }

    public CommentId(UUID scheduleId, Integer userId) {
        this.scheduleId = scheduleId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommentId)) {
            return false;
        }
        CommentId that = (CommentId) o;
        return Objects.equals(scheduleId, that.scheduleId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleId, userId);
    }
}
