package com.example.schedule_arranger.repository;

import com.example.schedule_arranger.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    List<Schedule> findByCreatedByOrderByUpdatedAtDesc(Integer createdBy);
}