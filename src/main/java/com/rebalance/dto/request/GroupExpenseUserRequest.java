package com.rebalance.dto.request;

import jakarta.validation.constraints.Digits;
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
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount should be positive")
    @Digits(integer = 12, fraction = 2, message = "Amount should have two digits after the decimal point")
    private Double amount;
    @Positive(message = "Multiplier should be positive")
    private Integer multiplier = 1;
}
