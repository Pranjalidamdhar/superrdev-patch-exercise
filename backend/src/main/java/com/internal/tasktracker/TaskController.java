package com.internal.tasktracker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private static final int MAX_PAGE_SIZE = 100;

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
        @RequestParam(required = false, defaultValue = "") String q,
        @RequestParam(required = false) String status,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Validate pagination input (page is 1-based)
        if (page < 1 || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "page must be >= 1 and pageSize must be between 1 and " + MAX_PAGE_SIZE));
        }

        // Normalize query input
        String query = q == null ? "" : q.trim();

        // Escape LIKE wildcards so user text is matched literally
        String escaped = query.toLowerCase()
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
        String searchTerm = "%" + escaped + "%";

        // Parse status filter, rejecting unknown values with a clear message
        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.trim().toUpperCase()).name();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid status. Allowed: OPEN, IN_PROGRESS, DONE"));
            }
        }

        System.out.println("[TaskController] q=\"" + query + "\" status=" + normalizedStatus
            + " page=" + page + " pageSize=" + pageSize);

        List<Task> allResults = taskRepository.searchTasks(searchTerm, normalizedStatus);

        // Slice the requested page (long math avoids int overflow on huge page numbers)
        long startLong = (long) (page - 1) * pageSize;
        int start = (int) Math.min(startLong, allResults.size());
        int end = Math.min(start + pageSize, allResults.size());
        List<Task> pageResults = allResults.subList(start, end);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}
