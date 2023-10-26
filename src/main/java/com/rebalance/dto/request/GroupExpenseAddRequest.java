package com.rebalance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class GroupExpenseAddRequest {
    private Long initiatorUserId;
    private Long groupId;
    private Double amount;
    private String description;
    private String category;
    private List<GroupExpenseUserRequest> users;
}
