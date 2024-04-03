package uz.task.manager.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {
    @Builder.Default
    private Boolean success = true;
    private String message;
    private Object data;
}
