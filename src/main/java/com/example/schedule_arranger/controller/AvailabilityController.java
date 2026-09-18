package com.example.schedule_arranger.controller;

import com.example.schedule_arranger.entity.Availability;
import com.example.schedule_arranger.entity.AvailabilityId;
import com.example.schedule_arranger.repository.AvailabilityRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/schedules")
public class AvailabilityController {

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityController(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @PostMapping("/{scheduleId}/users/{userId}/candidates/{candidateId}")
    public Map<String, Object> update(@PathVariable UUID scheduleId,
                                      @PathVariable Integer userId,
                                      @PathVariable Integer candidateId,
                                      @RequestBody(required = false) AvailabilityRequest body) {
        int availability = (body != null && body.availability() != null) ? body.availability() : 0;

        Availability entity = availabilityRepository
                .findById(new AvailabilityId(candidateId, userId))
                .orElseGet(Availability::new);
        entity.setCandidateId(candidateId);
        entity.setUserId(userId);
        entity.setScheduleId(scheduleId);
        entity.setAvailability(availability);
        availabilityRepository.save(entity);

        return Map.of("status", "OK", "availability", availability);
    }

    public record AvailabilityRequest(Integer availability) {
    }
}
