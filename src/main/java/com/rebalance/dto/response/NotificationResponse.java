package com.rebalance.dto.response;

import com.rebalance.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private Long initiatorUserId;
    private Long expenseId;
    private Long groupId;
    private List<String> message;
}
