package uz.task.manager.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotNull
    @Size(min = 6, max = 20, message = "Username size should be between 6 and 20")
    private String username;

    @NotNull
    @Size(min = 6, max = 20, message = "Password size should be between 6 and 20")
    private String password;

    @NotNull
    private String fullName;
}
