package com.rebalance.dto.request;

import com.rebalance.dto.common.ValidationConst;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class UserCreateRequest {
    @Email(message = "Email is required", regexp = ValidationConst.emailRegex)
    private String email;
    @NotNull(message = "Password is required")
    @Size(message = "Password length should be between 8 to 255 characters", min = 8, max = 255)
    @Pattern(message = "Password should contain at least one digit, one lowercase, uppercase and special character",
            regexp = ValidationConst.passwordRegex)
    private String password;
    @NotNull(message = "Nickname is required")
    @Size(message = "Nickname length should be between 1 to 255 characters", min = 1, max = 255)
    private String nickname;
    @NotNull(message = "Currency is required")
    @Pattern(message = "Currency should be an ISO 4217 compliant code", regexp = ValidationConst.currencyRegex)
    private String currency;
}
