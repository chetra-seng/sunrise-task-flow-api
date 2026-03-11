package com.chetraseng.sunrise_task_flow_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginRequest {
    @Email(message = "email must be valid")
    @NotEmpty(message = "email must be not be empty")
    private String email;

    @NotEmpty(message = "password must not be empty")
    @Length(message = "password must be at least 8", min = 8, max = 100)
    private String password;
}
