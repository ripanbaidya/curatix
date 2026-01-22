package com.curatix.api.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Register account request")
public record RegisterAccountRequest(

        @Schema(
                description = "Full name of the user",
                example = "John Doe",
                minLength = 2,
                maxLength = 100
        )
        @NotBlank(message = "Full name must not be blank")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        String fullName,

        @Schema(
                description = "Email address of the user",
                example = "john.doe@example.com",
                format = "email"
        )
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Invalid email format")
        String email,

        @Schema(
                description = "Account password",
                example = "StrongPass123",
                minLength = 8,
                maxLength = 20
        )
        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        String password
) {
}
