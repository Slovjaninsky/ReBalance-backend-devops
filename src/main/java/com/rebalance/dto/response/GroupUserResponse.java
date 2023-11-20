package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUserResponse {
    private Long id;
    private String nickname;
    private String email;
}
