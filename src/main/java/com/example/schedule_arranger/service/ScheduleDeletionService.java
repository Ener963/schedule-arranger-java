package com.example.schedule_arranger.service;

import com.example.schedule_arranger.repository.AvailabilityRepository;
import com.example.schedule_arranger.repository.CandidateRepository;
import com.example.schedule_arranger.repository.CommentRepository;
import com.example.schedule_arranger.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ScheduleDeletionService {

    private final AvailabilityRepository availabilityRepository;
    private final CandidateRepository candidateRepository;
    private final CommentRepository commentRepository;
    private final ScheduleRepository scheduleRepository;

    public ScheduleDeletionService(AvailabilityRepository availabilityRepository,
                                   CandidateRepository candidateRepository,
                                   CommentRepository commentRepository,
                                   ScheduleRepository scheduleRepository) {
        this.availabilityRepository = availabilityRepository;
        this.candidateRepository = candidateRepository;
        this.commentRepository = commentRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public void deleteScheduleAggregate(UUID scheduleId) {
        availabilityRepository.deleteByScheduleId(scheduleId);
        candidateRepository.deleteByScheduleId(scheduleId);
        commentRepository.deleteByScheduleId(scheduleId);
        scheduleRepository.deleteById(scheduleId);
    }
}