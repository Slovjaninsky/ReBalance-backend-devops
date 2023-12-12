package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationAllResponse {
    private Integer type;
    private Long initiatorUserId;
    private Long userAddedId;
    private Long expenseId;
    private Long groupId;
    private LocalDateTime date;
}
