package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithTokenResponse {
    private Long id;
    private String nickname;
    private String email;
    private String currency;
    private Long personalGroupId;
    private String token;
}
