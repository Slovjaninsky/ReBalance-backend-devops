package com.rebalance.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class GroupFavoriteRequest {
    @NotNull(message = "Group id is required")
    private Long groupId;
    @NotNull(message = "Favorite value is required")
    private Boolean favorite;
}
