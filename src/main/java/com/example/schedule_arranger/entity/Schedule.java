package com.example.schedule_arranger.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "schedules", indexes = @Index(name = "schedules_createdBy_idx", columnList = "createdBy"))
@Getter
@Setter
@NoArgsConstructor
public class Schedule {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID scheduleId;

    @Column(nullable = false, length = 255)
    private String scheduleName;

    @Column(nullable = false, columnDefinition = "text")
    private String memo;

    @Column(nullable = false)
    private Integer createdBy;

    @Column(nullable = false, columnDefinition = "timestamptz(6)")
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", referencedColumnName = "userId", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @OneToMany(mappedBy = "schedule")
    private List<Candidate> candidates = new ArrayList<>();

    @OneToMany(mappedBy = "schedule")
    private List<Comment> comments = new ArrayList<>();
}