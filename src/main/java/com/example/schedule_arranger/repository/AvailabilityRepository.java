package com.example.schedule_arranger.repository;

import com.example.schedule_arranger.entity.Availability;
import com.example.schedule_arranger.entity.AvailabilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AvailabilityRepository extends JpaRepository<Availability, AvailabilityId> {


    @Query("SELECT a FROM Availability a JOIN FETCH a.user WHERE a.scheduleId = :scheduleId ORDER BY a.candidateId ASC")
    List<Availability> findByScheduleIdWithUserOrderByCandidateIdAsc(@Param("scheduleId") UUID scheduleId);

    @Modifying
    @Query("DELETE FROM Availability a WHERE a.scheduleId = :scheduleId")
    void deleteByScheduleId(@Param("scheduleId") UUID scheduleId);
}