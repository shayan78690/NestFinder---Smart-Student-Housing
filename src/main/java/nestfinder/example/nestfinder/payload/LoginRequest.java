package nestfinder.example.nestfinder.payload;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
