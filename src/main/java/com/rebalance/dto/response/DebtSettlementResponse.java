package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebtSettlementResponse {
    private Long debtorId;
    private String debtorNickname;
    private Long creditorId;
    private String creditorNickname;
    private Double debt;
}
