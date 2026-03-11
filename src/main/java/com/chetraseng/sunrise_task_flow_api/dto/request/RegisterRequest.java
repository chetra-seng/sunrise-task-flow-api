package com.chetraseng.sunrise_task_flow_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegisterRequest {
    @NotEmpty(message = "first name must not be empty")
    @Length(message = "first name must be at least 8", min = 2, max = 100)
    private String firstName;

    @NotEmpty(message = "last name must not be empty")
    @Length(message = "last name must be at least 8", min = 2, max = 100)
    private String lastName;

    @NotEmpty(message = "email must not be empty")
    @Email(message = "email must be valid")
    private String email;

    @NotEmpty(message = "password must not be empty")
    @Length(message = "password must be at least 8", min = 8, max = 100)
    private String password;
}
