package com.rebalance.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class GroupExpensesGetRequest {
    @NotNull(message = "Group id is required")
    private Long groupId;

    @NotNull(message = "List of expense ids is required")
    @NotEmpty(message = "At least one expense id should be specified")
    private List<Long> expenseIds;
}
