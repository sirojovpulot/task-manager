package uz.task.manager.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import uz.task.manager.entity.Task;
import uz.task.manager.entity.enums.TaskPriority;
import uz.task.manager.entity.enums.TaskStatus;
import uz.task.manager.payload.ApiResponse;
import uz.task.manager.payload.TaskRequest;
import uz.task.manager.repository.TaskRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;
    private Task task;
    private List<Task> tasks = new ArrayList<>();
    private String title;
    private String content;
    private TaskPriority priority;
    private TaskStatus status;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate dueDate;

    @BeforeEach
    void setUp() {
        title = "";
        content = "";
        priority = TaskPriority.HIGH;
        status = TaskStatus.OPEN;
        category = "";
        startDate = LocalDate.of(2024, Month.APRIL, 20);
        endDate = LocalDate.of(2024, Month.MAY, 20);
        dueDate = LocalDate.of(2024, Month.APRIL, 20);

        task = Task.builder()
                .title(title)
                .content(content)
                .category(category)
                .dueDate(dueDate)
                .status(status)
                .priority(priority).build();

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
    void viewTasks() {
        Page<Task> taskPage = new PageImpl<>(tasks);
        String title = "t";
        String content = "";
        TaskPriority priority = TaskPriority.HIGH;
        TaskStatus status = TaskStatus.OPEN;
        String category = "";
        LocalDate startDate = LocalDate.of(2024, Month.APRIL, 20);
        LocalDate endDate = LocalDate.of(2024, Month.MAY, 20);
        Integer page = 0;
        Integer size = 10;

        given(taskRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(taskPage);

        ApiResponse response = taskService.viewTasks(page, size, title, content, priority, status, startDate, endDate, category);

        assertEquals(taskPage, response.getData());
    }

    @Test
    void viewTasksWithNoData() {
        Page<Task> taskPage = new PageImpl<>(List.of());
        String title = "a";
        String content = "";
        TaskPriority priority = TaskPriority.HIGH;
        TaskStatus status = TaskStatus.OPEN;
        String category = "";
        LocalDate startDate = LocalDate.of(2024, Month.APRIL, 20);
        LocalDate endDate = LocalDate.of(2024, Month.MAY, 20);
        Integer page = 0;
        Integer size = 10;

        given(taskRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(taskPage);

        ApiResponse response = taskService.viewTasks(page, size, title, content, priority, status, startDate, endDate, category);

        assertEquals(((Page) response.getData()).getContent().size(), 0);
    }

    @Test
    void createTask() {
        when(taskRepository.save(task)).thenReturn(task);

        ApiResponse response = taskService.createTask(new TaskRequest(title, content, priority, dueDate, category));

        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task taskArgumentCaptorValue = taskArgumentCaptor.getValue();
        assertThat(taskArgumentCaptorValue).isEqualTo(task);

        assertEquals("Task is created successfully", response.getMessage());
    }

    @Test
    void updateTask() {
        task.setId(1L);
        String newContent = "new content";
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        task.setContent(newContent);
        when(taskRepository.save(task)).thenReturn(task);

        ApiResponse response = taskService.updateTask(1L, new TaskRequest(title, newContent, priority, dueDate, category));

        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task taskArgumentCaptorValue = taskArgumentCaptor.getValue();
        assertThat(taskArgumentCaptorValue).isEqualTo(task);

        assertEquals("Task is updated successfully", response.getMessage());
    }

    @Test
    void deleteTask() {
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

        ApiResponse response = taskService.deleteTask(1L);

        assertEquals("Task is deleted successfully", response.getMessage());
    }
}