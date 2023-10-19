package com.rebalance.mapper;

import com.rebalance.dto.request.GroupExpenseAddRequest;
import com.rebalance.dto.request.GroupExpenseUserRequest;
import com.rebalance.dto.response.ExpenseResponse;
import com.rebalance.entities.Expense;
import com.rebalance.entities.ExpenseUsers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ExpenseMapper {
    ExpenseResponse expenseToResponse(Expense expense);

    @Mapping(target = "user.id", source = "userId")
    ExpenseUsers groupExpenseUserRequestToExpenseUser(GroupExpenseUserRequest request);

    List<ExpenseUsers> groupExpenseUserRequestToExpenseUserList(List<GroupExpenseUserRequest> request);

    @Mapping(target = "user.id", source = "initiatorUserId")
    @Mapping(target = "group.id", source = "groupId")
    Expense groupExpenseRequestToExpense(GroupExpenseAddRequest request);
}
