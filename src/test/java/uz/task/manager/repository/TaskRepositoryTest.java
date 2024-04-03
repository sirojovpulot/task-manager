package uz.task.manager.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import uz.task.manager.entity.Task;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void itShouldSaveTask() {
        Task task = Task.builder()
                .dueDate(LocalDate.of(2024, Month.APRIL, 5))
                .category("category")
                .content("content")
                .title("title").build();
        taskRepository.save(task);
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).isEqualTo(Arrays.asList(task));
    }
}