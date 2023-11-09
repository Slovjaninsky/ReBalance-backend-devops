package com.rebalance.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEditRequest {
    @NotNull(message = "Category id is required")
    private Long id;
    @NotNull(message = "Category name is required")
    private String name;
}
