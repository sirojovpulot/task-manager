package uz.task.manager.service;

import uz.task.manager.entity.enums.TaskPriority;
import uz.task.manager.entity.enums.TaskStatus;
import uz.task.manager.payload.ApiResponse;
import uz.task.manager.payload.TaskRequest;
import uz.task.manager.payload.TaskStatusRequest;

import java.time.LocalDate;

public interface TaskService {
    ApiResponse viewTasks(Integer page,
                          Integer size,
                          String title,
                          String content,
                          TaskPriority priority,
                          TaskStatus status,
                          LocalDate startDate,
                          LocalDate endDate,
                          String category);

    ApiResponse createTask(TaskRequest request);

    ApiResponse updateTask(Long id, TaskRequest request);

    ApiResponse deleteTask(Long id);

    ApiResponse updateTaskStatus(Long id, TaskStatusRequest request);
}
