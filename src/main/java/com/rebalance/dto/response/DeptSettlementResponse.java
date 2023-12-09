package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptSettlementResponse {
    private Long deptorId;
    private String deptorNickname;
    private Long creditorId;
    private String creditorNickname;
    private Double dept;
}
