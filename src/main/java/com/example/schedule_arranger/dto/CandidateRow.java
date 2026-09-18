package com.example.schedule_arranger.dto;

import java.util.List;

public record CandidateRow(Integer candidateId, String candidateName, List<AvailabilityCell> cells) {
}