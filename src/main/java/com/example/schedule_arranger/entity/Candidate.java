package com.example.schedule_arranger.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "candidates",
        uniqueConstraints = @UniqueConstraint(name = "candidates_candidateId_scheduleId_key", columnNames = {"candidateId", "scheduleId"}),
        indexes = @Index(name = "candidates_scheduleId_idx", columnList = "scheduleId")
)
@Getter
@Setter
@NoArgsConstructor
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer candidateId;

    @Column(nullable = false, length = 255)
    private String candidateName;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleId", referencedColumnName = "scheduleId", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Schedule schedule;

    @OneToMany(mappedBy = "candidate")
    private List<Availability> availabilities = new ArrayList<>();
}
