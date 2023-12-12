package com.rebalance.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class NotificationAllRequest {
    @NotNull(message = "Date is required")
    private Date date;
}
