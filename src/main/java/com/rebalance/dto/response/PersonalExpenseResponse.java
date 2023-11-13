package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalExpenseResponse {
    private Long id;
    private Double amount;
    private String description;
    private LocalDateTime date;
    private String category;
}
