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
public class PersonalExpenseEditRequest {
    @NotNull(message = "Expense id is required")
    private Long expenseId;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount should be possible")
    private Double amount;
    @NotNull(message = "Description is required")
    private String description;
    @NotNull(message = "Category is required")
    private String category;
}
