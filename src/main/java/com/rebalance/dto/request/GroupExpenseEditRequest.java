package com.rebalance.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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
    private Double amount;
    @NotNull(message = "Description is required")
    private String description;
    @NotNull(message = "Category is required")
    private String category;
    @PastOrPresent(message = "Date should not be in future")
    private LocalDateTime date;
    @NotNull(message = "Users is required")
    @NotEmpty(message = "There should be at least one debtor")
    private List<GroupExpenseUserRequest> users;
}
