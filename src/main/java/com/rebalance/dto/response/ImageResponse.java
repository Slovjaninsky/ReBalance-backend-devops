package com.rebalance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageResponse {

    private long expenseId;
    private String base64;

}