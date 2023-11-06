package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupExpenseResponse {
    private Long id;
    private Double amount;
    private String description;
    private LocalDateTime date;
    private String category;
    private List<GroupExpenseUserResponse> users;
}
