package uz.task.manager.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    @NotNull
    @Size(min = 6, max = 20, message = "Username size should be between 6 and 20")
    private String username;

    @NotNull
    @Size(min = 6, max = 20, message = "Password size should be between 6 and 20")
    private String password;
}
