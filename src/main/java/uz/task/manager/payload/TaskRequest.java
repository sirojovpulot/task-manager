package uz.task.manager.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.task.manager.entity.enums.TaskPriority;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private TaskPriority priority;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private String category;

}
