package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupExpenseUserResponse {
    private Long id;
    private Double amount;
    private Integer multiplier;
    private Long userId;
}
