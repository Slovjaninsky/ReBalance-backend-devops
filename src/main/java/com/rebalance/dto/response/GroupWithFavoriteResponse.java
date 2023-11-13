package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupWithFavoriteResponse {
    private Long id;
    private String name;
    private String currency;
    private Boolean favorite;
}
