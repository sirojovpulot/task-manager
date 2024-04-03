package uz.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import uz.task.manager.entity.Task;
import uz.task.manager.entity.enums.TaskPriority;
import uz.task.manager.entity.enums.TaskStatus;
import uz.task.manager.payload.ApiResponse;
import uz.task.manager.payload.TaskRequest;
import uz.task.manager.payload.TaskStatusRequest;
import uz.task.manager.repository.TaskRepository;
import uz.task.manager.repository.TaskSpecification;
import uz.task.manager.service.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public ApiResponse viewTasks(Integer page,
                                 Integer size,
                                 String title,
                                 String content,
                                 TaskPriority priority,
                                 TaskStatus status,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 String category) {

        Specification<Task> where = Specification
                .where(title.isEmpty() ? null : TaskSpecification.titleContains(title))
                .and(content.isEmpty() ? null : TaskSpecification.contentContains(content))
                .and(priority == null ? null : TaskSpecification.priorityEqual(priority))
                .and(status == null ? null : TaskSpecification.statusEqual(status))
                .and(category.isEmpty() ? null : TaskSpecification.categoryContains(category))
                .and(startDate == null && endDate == null ? null : TaskSpecification.dueDateBetween(startDate, endDate));

        Page<Task> tasks = taskRepository.findAll(where, PageRequest.of(page, size, Sort.Direction.ASC, "createdAt"));

        return ApiResponse.builder()
                .data(tasks)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse createTask(TaskRequest request) {
        Task task = Task.builder()
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .category(request.getCategory().trim())
                .content(request.getContent().trim())
                .title(request.getTitle().trim())
                .build();
        taskRepository.save(task);

        return ApiResponse.builder()
                .message("Task is created successfully")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task is not found"));
        task.setTitle(request.getTitle().trim());
        task.setContent(request.getContent().trim());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setCategory(request.getCategory().trim());
        task.setUpdatedAt(LocalDateTime.now());
        task.setStatus(request.getStatus());
        taskRepository.save(task);
        return ApiResponse.builder()
                .message("Task is updated successfully")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task is not found"));
        taskRepository.delete(task);
        return ApiResponse.builder()
                .message("Task is deleted successfully")
                .build();
    }

    @Override
    public ApiResponse updateTaskStatus(Long id, TaskStatusRequest request) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task is not found"));
        task.setStatus(request.getStatus());
        taskRepository.save(task);
        return ApiResponse.builder()
                .message("Task status is updated successfully")
                .build();
    }
}
