package com.rebalance.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class GroupExpenseEditRequest {
    @NotNull(message = "Expense id is required")
    private Long expenseId;
    @NotNull(message = "Initiator user id is required")
    private Long initiatorUserId;
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
    @NotNull(message = "Users is required")
    @NotEmpty(message = "There should be at least one debtor")
    @Valid
    private List<GroupExpenseUserRequest> users;
}
