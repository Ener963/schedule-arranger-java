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
import com.example.schedule_arranger.service.ScheduleDeletionService;
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
    private static final String[] BUTTON_STYLES = {"btn-danger", "btn-secondary", "btn-success"};

    private final ScheduleRepository scheduleRepository;
    private final CandidateRepository candidateRepository;
    private final AvailabilityRepository availabilityRepository;
    private final CommentRepository commentRepository;
    private final ScheduleDeletionService scheduleDeletionService;

    public ScheduleController(ScheduleRepository scheduleRepository,
                              CandidateRepository candidateRepository,
                              AvailabilityRepository availabilityRepository,
                              CommentRepository commentRepository,
                              ScheduleDeletionService scheduleDeletionService) {
        this.scheduleRepository = scheduleRepository;
        this.candidateRepository = candidateRepository;
        this.availabilityRepository = availabilityRepository;
        this.commentRepository = commentRepository;
        this.scheduleDeletionService = scheduleDeletionService;
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

        UUID scheduleId = UUID.randomUUID();
        String truncatedName = truncate(scheduleName, NAME_MAX_LENGTH);

        Schedule schedule = new Schedule();
        schedule.setScheduleId(scheduleId);
        schedule.setScheduleName(!truncatedName.isEmpty() ? truncatedName : "（名称未設定）");
        schedule.setMemo(memo);
        schedule.setCreatedBy(userId);
        schedule.setUpdatedAt(OffsetDateTime.now());
        scheduleRepository.save(schedule);

        createCandidates(parseCandidateNames(candidates), scheduleId);

        return "redirect:/schedules/" + scheduleId;
    }

    @GetMapping("/{scheduleId}")
    public String show(@PathVariable UUID scheduleId,
                       @AuthenticationPrincipal OAuth2User principal,
                       Model model) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Candidate> candidates = candidateRepository.findByScheduleIdOrderByCandidateIdAsc(scheduleId);

        List<Availability> availabilities = availabilityRepository.findByScheduleIdWithUserOrderByCandidateIdAsc(scheduleId);

        Map<Integer, Map<Integer, Integer>> availabilityMapMap = new LinkedHashMap<>();
        for (Candidate candidate : candidates) {
            availabilityMapMap.put(candidate.getCandidateId(), new HashMap<>());
        }

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

        List<UserSummary> users = new ArrayList<>(userMap.values());

        List<CandidateRow> rows = new ArrayList<>();
        for (Candidate candidate : candidates) {
            Map<Integer, Integer> inner = availabilityMapMap.get(candidate.getCandidateId());
            List<AvailabilityCell> cells = new ArrayList<>();
            for (UserSummary u : users) {
                int availability = inner.getOrDefault(u.userId(), 0);
                String label = AVAILABILITY_LABELS[availability];
                String cssClass = "availability-toggle-button btn btn-lg " + BUTTON_STYLES[availability];
                boolean editable = u.userId().equals(viewerUserId);
                cells.add(new AvailabilityCell(u.userId(), candidate.getCandidateId(), availability, label, cssClass, editable));
            }
            rows.add(new CandidateRow(candidate.getCandidateId(), candidate.getCandidateName(), cells));
        }

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
        model.addAttribute("isMine", isMine(viewerUserId, schedule));
        return "schedules/show";
    }

    @GetMapping("/{scheduleId}/edit")
    public String edit(@PathVariable UUID scheduleId,
                       @AuthenticationPrincipal OAuth2User principal,
                       Model model) {
        Integer viewerUserId = ((Number) principal.getAttribute("id")).intValue();
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (!isMine(viewerUserId, schedule)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<Candidate> candidates = candidateRepository.findByScheduleIdOrderByCandidateIdAsc(scheduleId);

        model.addAttribute("schedule", schedule);
        model.addAttribute("candidates", candidates);
        return "schedules/edit";
    }

    @PostMapping("/{scheduleId}/update")
    public String update(@PathVariable UUID scheduleId,
                         @AuthenticationPrincipal OAuth2User principal,
                         @RequestParam(defaultValue = "") String scheduleName,
                         @RequestParam(defaultValue = "") String memo,
                         @RequestParam(defaultValue = "") String candidates) {
        Integer viewerUserId = ((Number) principal.getAttribute("id")).intValue();
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (!isMine(viewerUserId, schedule)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        String truncatedName = truncate(scheduleName, NAME_MAX_LENGTH);
        schedule.setScheduleName(!truncatedName.isEmpty() ? truncatedName : "（名称未設定）");
        schedule.setMemo(memo);
        schedule.setUpdatedAt(OffsetDateTime.now());
        scheduleRepository.save(schedule);

        List<String> candidateNames = parseCandidateNames(candidates);
        if (!candidateNames.isEmpty()) {
            createCandidates(candidateNames, schedule.getScheduleId());
        }

        return "redirect:/schedules/" + schedule.getScheduleId();
    }

    @PostMapping("/{scheduleId}/delete")
    public String delete(@PathVariable UUID scheduleId,
                         @AuthenticationPrincipal OAuth2User principal) {
        Integer viewerUserId = ((Number) principal.getAttribute("id")).intValue();
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (!isMine(viewerUserId, schedule)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        scheduleDeletionService.deleteScheduleAggregate(schedule.getScheduleId());
        return "redirect:/";
    }

    private boolean isMine(Integer userId, Schedule schedule) {
        return schedule != null && schedule.getCreatedBy().equals(userId);
    }

    private List<String> parseCandidateNames(String candidatesStr) {
        return Arrays.stream(candidatesStr.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private void createCandidates(List<String> candidateNames, UUID scheduleId) {
        for (String candidateName : candidateNames) {
            Candidate candidate = new Candidate();
            candidate.setCandidateName(truncate(candidateName, NAME_MAX_LENGTH));
            candidate.setScheduleId(scheduleId);
            candidateRepository.save(candidate);
        }
    }

    private String truncate(String value, int maxLength) {
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}