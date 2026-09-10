package com.example.schedule_arranger.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "availabilities", indexes = @Index(name = "availabilities_scheduleId_idx", columnList = "scheduleId"))
@IdClass(AvailabilityId.class)
@Getter
@Setter
@NoArgsConstructor
public class Availability {

    @Id
    @Column(nullable = false)
    private Integer candidateId;

    @Id
    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer availability = 0;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "candidateId", referencedColumnName = "candidateId", insertable = false, updatable = false),
            @JoinColumn(name = "scheduleId", referencedColumnName = "scheduleId", insertable = false, updatable = false)
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}