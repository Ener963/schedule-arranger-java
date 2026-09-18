package com.example.schedule_arranger.repository;

import com.example.schedule_arranger.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, Integer> {

    List<Candidate> findByScheduleIdOrderByCandidateIdAsc(UUID scheduleId);

    @Modifying
    @Query("DELETE FROM Candidate c WHERE c.scheduleId = :scheduleId")
    void deleteByScheduleId(@Param("scheduleId") UUID scheduleId);
}