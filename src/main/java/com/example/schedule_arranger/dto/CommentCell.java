package com.example.schedule_arranger.dto;

/**
 * 出欠表のコメント行の1マスに相当する表示用DTO（schedules.js の commentMap 参照部分）。
 * editable は「閲覧ユーザー本人の列かどうか」（本人なら編集ボタンを表示）。
 */
public record CommentCell(Integer userId, String comment, boolean editable) {
}