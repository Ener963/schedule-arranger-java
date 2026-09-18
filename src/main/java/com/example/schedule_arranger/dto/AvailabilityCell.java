package com.example.schedule_arranger.dto;


public record AvailabilityCell(Integer userId, Integer candidateId, int availability, String label, String cssClass, boolean editable) {
}