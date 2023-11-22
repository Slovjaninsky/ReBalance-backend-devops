package com.rebalance.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class PersonalExpenseEditRequest {
    @NotNull(message = "Expense id is required")
    private Long expenseId;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount should be possible")
    @Digits(integer = 12, fraction = 2, message = "Amount should have two digits after the decimal point")
    private Double amount;
    @NotNull(message = "Description is required")
    private String description;
    @NotNull(message = "Category is required")
    private String category;
    @PastOrPresent(message = "Date should not be in future")
    private LocalDateTime date;
}
