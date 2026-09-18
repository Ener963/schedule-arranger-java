package com.example.schedule_arranger.dto;

import java.util.List;

/**
 * 出欠表の1行（候補日程 + 各ユーザーのセル）に相当する表示用DTO。
 */
public record CandidateRow(Integer candidateId, String candidateName, List<AvailabilityCell> cells) {
}