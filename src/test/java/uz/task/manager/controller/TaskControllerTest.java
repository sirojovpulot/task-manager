package uz.task.manager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uz.task.manager.entity.Task;
import uz.task.manager.entity.enums.TaskPriority;
import uz.task.manager.entity.enums.TaskStatus;
import uz.task.manager.payload.ApiResponse;
import uz.task.manager.payload.TaskRequest;
import uz.task.manager.service.TaskService;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;
    private List<Task> tasks = new ArrayList<>();

    @BeforeEach
    void setUp() {
        tasks.addAll(Arrays.asList(
                Task.builder()
                        .title("title1")
                        .content("content1")
                        .category("category1")
                        .dueDate(LocalDate.of(2024, Month.APRIL, 20))
                        .status(TaskStatus.OPEN)
                        .priority(TaskPriority.NORMAL).build(),
                Task.builder()
                        .title("title2")
                        .content("content2")
                        .category("category2")
                        .dueDate(LocalDate.of(2024, Month.MAY, 20))
                        .status(TaskStatus.COMPLETED)
                        .priority(TaskPriority.HIGH).build()
        ));
    }

    @Test
    void viewTasks_WithDefaultParameters_ShouldReturnOkResponse() {
        PageImpl<Task> taskPage = new PageImpl<>(tasks);
        when(taskService.viewTasks(0, 10, "", "", null, null, null, null, ""))
                .thenReturn(ApiResponse.builder()
                        .data(taskPage).build());

        ResponseEntity<ApiResponse> response = taskController.viewTasks(0, 10, "", "", null, null, null, null, "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskPage, response.getBody().getData());
    }

    @Test
    void createTask_WithValidRequest_ShouldReturnOkResponse() {
        TaskRequest taskRequest = new TaskRequest();

        when(taskService.createTask(taskRequest)).thenReturn(ApiResponse.builder()
                .message("Task is created successfully")
                .build());

        ResponseEntity<ApiResponse> response = taskController.createTask(taskRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task is created successfully", response.getBody().getMessage());
    }

    @Test
    void updateTask_WithValidRequest_ShouldReturnOkResponse() {
        Long taskId = 1L;
        TaskRequest taskRequest = new TaskRequest();

        when(taskService.updateTask(taskId, taskRequest)).thenReturn(ApiResponse.builder()
                .message("Task is updated successfully")
                .build());

        ResponseEntity<ApiResponse> response = taskController.updateTask(taskId, taskRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task is updated successfully", response.getBody().getMessage());
    }

    @Test
    void deleteTask_WithValidId_ShouldReturnOkResponse() {
        Long taskId = 1L;

        when(taskService.deleteTask(taskId)).thenReturn(ApiResponse.builder()
                .message("Task is deleted successfully")
                .build());

        ResponseEntity<ApiResponse> response = taskController.deleteTask(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task is deleted successfully", response.getBody().getMessage());
    }
}