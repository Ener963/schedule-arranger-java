package com.example.schedule_arranger.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "comments")
@IdClass(CommentId.class)
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @Column(nullable = false, columnDefinition = "uuid")
    private UUID scheduleId;

    @Id
    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false, length = 255)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleId", referencedColumnName = "scheduleId", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Schedule schedule;
}