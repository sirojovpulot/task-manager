package uz.task.manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.task.manager.entity.enums.TaskPriority;
import uz.task.manager.entity.enums.TaskStatus;
import uz.task.manager.payload.ApiResponse;
import uz.task.manager.payload.TaskRequest;
import uz.task.manager.service.TaskService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/task")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<ApiResponse> viewTasks(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "title", defaultValue = "") String title,
            @RequestParam(name = "content", defaultValue = "") String content,
            @RequestParam(name = "priority", required = false) TaskPriority priority,
            @RequestParam(name = "status", required = false) TaskStatus status,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            @RequestParam(name = "category", defaultValue = "") String category) {
        return ResponseEntity.ok(
                taskService.viewTasks(
                        page,
                        size,
                        title,
                        content,
                        priority,
                        status,
                        startDate,
                        endDate,
                        category));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createTask(@RequestBody @Valid TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> updateTask(@PathVariable Long id, @RequestBody @Valid TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.deleteTask(id));
    }

}
