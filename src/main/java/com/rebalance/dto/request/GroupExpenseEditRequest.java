package com.rebalance.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.Date;
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
    private BigDecimal amount;
    @NotNull(message = "Description is required")
    private String description;
    @NotNull(message = "Category is required")
    private String category;
    private Date date;
    @NotNull(message = "Users is required")
    @NotEmpty(message = "There should be at least one debtor")
    @Valid
    private List<GroupExpenseUserRequest> users;
}
