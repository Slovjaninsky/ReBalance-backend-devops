package com.rebalance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class PersonalExpenseAddRequest {
    private Long initiatorUserId;
    private Long groupId;
    private Double amount;
    private String description;
    private String category;
}
