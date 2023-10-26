package com.rebalance.dto.request;

import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class GroupExpenseUserRequest {
    @NotNull(message = "Usr id is required")
    private Long userId;
    @NotNull(message = "Amount is required")
    @Negative(message = "Amount should be negative")
    private Double amount;
}
