package com.rebalance.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class GroupExpenseUserRequest {
    @NotNull(message = "User id is required")
    private Long userId;
    @NotNull(message = "Multiplier is required")
    @Positive(message = "Multiplier should be positive")
    private Integer multiplier = 1;
}
