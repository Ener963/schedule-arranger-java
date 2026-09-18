package com.example.schedule_arranger.dto;

/**
 * schedules.js の userMap の値（{ userId, username }）に相当する表示用DTO。
 */
public record UserSummary(Integer userId, String username) {
}