package com.example.schedule_arranger.entity;

import java.io.Serializable;
import java.util.Objects;

public class AvailabilityId implements Serializable {

    private Integer candidateId;
    private Integer userId;

    public AvailabilityId() {
    }

    public AvailabilityId(Integer candidateId, Integer userId) {
        this.candidateId = candidateId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AvailabilityId)) {
            return false;
        }
        AvailabilityId that = (AvailabilityId) o;
        return Objects.equals(candidateId, that.candidateId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(candidateId, userId);
    }
}
