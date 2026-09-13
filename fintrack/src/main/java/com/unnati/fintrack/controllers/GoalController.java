package com.unnati.fintrack.controllers;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unnati.fintrack.entity.Goal;
import com.unnati.fintrack.enums.GoalPriority;
import com.unnati.fintrack.enums.GoalStatus;
import com.unnati.fintrack.services.GoalService;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // =========================================================
    // GET ALL GOALS
    // GET /api/goals
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals() {

        return ResponseEntity.ok(
                goalService.findAll()
        );
    }

    // =========================================================
    // GET GOALS BY PRIORITY
    // GET /api/goals/priority/{priority}
    // =========================================================
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Goal>> getGoalsByPriority(
            @PathVariable GoalPriority priority) {

        return ResponseEntity.ok(
                goalService.findByPriority(priority)
        );
    }

    // =========================================================
    // GET GOALS BY STATUS
    // GET /api/goals/status/{status}
    // =========================================================
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Goal>> getGoalsByStatus(
            @PathVariable GoalStatus status) {

        return ResponseEntity.ok(
                goalService.findByStatus(status)
        );
    }

    // =========================================================
    // GET GOALS BY EXACT DEADLINE
    // GET /api/goals/deadline/{deadline}
    // =========================================================
    @GetMapping("/deadline/{deadline}")
    public ResponseEntity<List<Goal>> getGoalsByDeadline(
            @PathVariable LocalDate deadline) {

        return ResponseEntity.ok(
                goalService.findByDeadline(deadline)
        );
    }

    // =========================================================
    // GET GOALS BY DEADLINE RANGE
    // GET /api/goals/date-range?startDate=...&endDate=...
    // =========================================================
    @GetMapping("/date-range")
    public ResponseEntity<List<Goal>> getGoalsByDeadlineRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(
                goalService.findByDeadlineBetween(
                        startDate,
                        endDate
                )
        );
    }

    // =========================================================
    // GET GOALS BY NAME
    // GET /api/goals/name/{goalName}
    // =========================================================
    @GetMapping("/name/{goalName}")
    public ResponseEntity<List<Goal>> getGoalsByName(
            @PathVariable String goalName) {

        return ResponseEntity.ok(
                goalService.findByGoalName(goalName)
        );
    }

    // =========================================================
    // GET GOAL BY ID
    // GET /api/goals/{id}
    // =========================================================
    
    @GetMapping("/deadline-before")
    public ResponseEntity<List<Goal>> getGoalsByDeadlineBefore(
            @RequestParam LocalDate deadline) {

        return ResponseEntity.ok(
                goalService.findByDeadlineBefore(deadline)
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                goalService.findById(id)
        );
    }

    // =========================================================
    // CREATE GOAL
    // POST /api/goals
    // =========================================================
    @PostMapping
    public ResponseEntity<Goal> createGoal(
            @RequestBody Goal goal) {

        Goal savedGoal = goalService.save(goal);

        URI location = URI.create(
                "/api/goals/" + savedGoal.getId()
        );

        return ResponseEntity
                .created(location)
                .body(savedGoal);
    }

    // =========================================================
    // UPDATE GOAL
    // PUT /api/goals/{id}
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(
            @PathVariable Long id,
            @RequestBody Goal goal) {

        return ResponseEntity.ok(
                goalService.update(id, goal)
        );
    }

    // =========================================================
    // DELETE GOAL
    // DELETE /api/goals/{id}
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id) {

        goalService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}