package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupResponse {
    private Long id;
    private Long userId;
    private Long groupId;
    private Boolean favorite;
}
