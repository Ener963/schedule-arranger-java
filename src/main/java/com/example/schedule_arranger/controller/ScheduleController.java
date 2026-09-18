package com.example.schedule_arranger.controller;

import com.example.schedule_arranger.dto.AvailabilityCell;
import com.example.schedule_arranger.dto.CandidateRow;
import com.example.schedule_arranger.dto.CommentCell;
import com.example.schedule_arranger.dto.UserSummary;
import com.example.schedule_arranger.entity.Availability;
import com.example.schedule_arranger.entity.Candidate;
import com.example.schedule_arranger.entity.Comment;
import com.example.schedule_arranger.entity.Schedule;
import com.example.schedule_arranger.repository.AvailabilityRepository;
import com.example.schedule_arranger.repository.CandidateRepository;
import com.example.schedule_arranger.repository.CommentRepository;
import com.example.schedule_arranger.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private static final int NAME_MAX_LENGTH = 255;
    private static final String[] AVAILABILITY_LABELS = {"欠", "？", "出"};

    private final ScheduleRepository scheduleRepository;
    private final CandidateRepository candidateRepository;
    private final AvailabilityRepository availabilityRepository;
    private final CommentRepository commentRepository;

    public ScheduleController(ScheduleRepository scheduleRepository,
                              CandidateRepository candidateRepository,
                              AvailabilityRepository availabilityRepository,
                              CommentRepository commentRepository) {
        this.scheduleRepository = scheduleRepository;
        this.candidateRepository = candidateRepository;
        this.availabilityRepository = availabilityRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/new")
    public String newSchedule() {
        return "schedules/new";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal OAuth2User principal,
                         @RequestParam(defaultValue = "") String scheduleName,
                         @RequestParam(defaultValue = "") String memo,
                         @RequestParam(defaultValue = "") String candidates) {
        Integer userId = ((Number) principal.getAttribute("id")).intValue();

        // 予定を登録
        UUID scheduleId = UUID.randomUUID();
        String truncatedName = truncate(scheduleName, NAME_MAX_LENGTH);

        Schedule schedule = new Schedule();
        schedule.setScheduleId(scheduleId);
        schedule.setScheduleName(!truncatedName.isEmpty() ? truncatedName : "（名称未設定）");
        schedule.setMemo(memo);
        schedule.setCreatedBy(userId);
        schedule.setUpdatedAt(OffsetDateTime.now());
        scheduleRepository.save(schedule);

        // 候補日程を登録
        List<String> candidateNames = Arrays.stream(candidates.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        for (String candidateName : candidateNames) {
            Candidate candidate = new Candidate();
            candidate.setCandidateName(truncate(candidateName, NAME_MAX_LENGTH));
            candidate.setScheduleId(scheduleId);
            candidateRepository.save(candidate);
        }

        // 作成した予定のページにリダイレクト
        return "redirect:/schedules/" + scheduleId;
    }

    @GetMapping("/{scheduleId}")
    public String show(@PathVariable UUID scheduleId,
                       @AuthenticationPrincipal OAuth2User principal,
                       Model model) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Candidate> candidates = candidateRepository.findByScheduleIdOrderByCandidateIdAsc(scheduleId);

        // データベースからその予定の全ての出欠を取得する
        List<Availability> availabilities = availabilityRepository.findByScheduleIdWithUserOrderByCandidateIdAsc(scheduleId);

        // 各候補日程に対する各ユーザの出欠を入れ子の Map にして格納するための Map を作る。
        // key: candidateId, value: Map (key: userId, value: availability)
        Map<Integer, Map<Integer, Integer>> availabilityMapMap = new LinkedHashMap<>();
        for (Candidate candidate : candidates) {
            availabilityMapMap.put(candidate.getCandidateId(), new HashMap<>());
        }

        // 閲覧ユーザと、出欠を登録したユーザ情報を格納するための Map を作る。
        Map<Integer, UserSummary> userMap = new LinkedHashMap<>();
        Integer viewerUserId = ((Number) principal.getAttribute("id")).intValue();
        String viewerLogin = principal.getAttribute("login");
        userMap.put(viewerUserId, new UserSummary(viewerUserId, viewerLogin));

        for (Availability a : availabilities) {
            Map<Integer, Integer> inner = availabilityMapMap.get(a.getCandidateId());
            if (inner != null) {
                inner.put(a.getUserId(), a.getAvailability());
            }
            userMap.put(a.getUserId(), new UserSummary(a.getUserId(), a.getUser().getUsername()));
        }

        // 閲覧ユーザと、出欠を登録したユーザを合わせた全ユーザの配列を作る
        List<UserSummary> users = new ArrayList<>(userMap.values());

        List<CandidateRow> rows = new ArrayList<>();
        for (Candidate candidate : candidates) {
            Map<Integer, Integer> inner = availabilityMapMap.get(candidate.getCandidateId());
            List<AvailabilityCell> cells = new ArrayList<>();
            for (UserSummary u : users) {
                // 出欠が未登録の場合は「欠席」と表示する。
                int availability = inner.getOrDefault(u.userId(), 0);
                String label = AVAILABILITY_LABELS[availability];
                boolean editable = u.userId().equals(viewerUserId);
                cells.add(new AvailabilityCell(u.userId(), candidate.getCandidateId(), availability, label, "", editable));
            }
            rows.add(new CandidateRow(candidate.getCandidateId(), candidate.getCandidateName(), cells));
        }

        // コメント取得
        List<Comment> comments = commentRepository.findByScheduleId(scheduleId);
        Map<Integer, String> commentMap = new HashMap<>();
        for (Comment comment : comments) {
            commentMap.put(comment.getUserId(), comment.getComment());
        }

        List<CommentCell> commentCells = new ArrayList<>();
        for (UserSummary u : users) {
            String comment = commentMap.get(u.userId());
            boolean editable = u.userId().equals(viewerUserId);
            commentCells.add(new CommentCell(u.userId(), comment, editable));
        }

        model.addAttribute("schedule", schedule);
        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("users", users);
        model.addAttribute("rows", rows);
        model.addAttribute("commentCells", commentCells);
        return "schedules/show";
    }

    private String truncate(String value, int maxLength) {
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}