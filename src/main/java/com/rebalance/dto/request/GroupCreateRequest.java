package com.rebalance.dto.request;

import com.rebalance.dto.common.ValidationConst;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class GroupCreateRequest {
    @NotNull(message = "Group name is required")
    @Size(message = "Group name length should be between 1 to 255 characters", min = 1, max = 255)
    private String name;
    @NotNull(message = "Currency is required")
    @Pattern(message = "Currency should be an ISO 4217 compliant code", regexp = ValidationConst.currencyRegex)
    private String currency;
    @NotNull(message = "Creator id is required")
    private Long creatorId;
}
