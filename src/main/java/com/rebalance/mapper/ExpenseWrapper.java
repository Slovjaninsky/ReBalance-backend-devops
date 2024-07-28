package com.rebalance.mapper;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class ExpenseWrapper {
    private Long[] expenseIds;

    @Override
    public String toString() {
        return "ExpenseWrapper{" +
                "expenseIds=" + Arrays.toString(expenseIds) +
                '}';
    }
}