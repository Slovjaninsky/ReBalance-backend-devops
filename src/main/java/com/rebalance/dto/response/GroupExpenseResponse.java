package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupExpenseResponse {
    private Long id;
    private Long initiatorUserId;
    private Long addedByUserId;
    private Double amount;
    private String description;
    private LocalDate date;
    private String category;
    private List<GroupExpenseUserResponse> users;
}
