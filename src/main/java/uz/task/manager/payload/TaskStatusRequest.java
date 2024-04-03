package uz.task.manager.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.task.manager.entity.enums.TaskStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusRequest {
    @NotNull
    private TaskStatus status;
}
