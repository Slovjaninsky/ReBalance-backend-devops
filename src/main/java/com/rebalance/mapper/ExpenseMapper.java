package com.rebalance.mapper;

import com.rebalance.dto.request.GroupExpenseAddRequest;
import com.rebalance.dto.request.GroupExpenseEditRequest;
import com.rebalance.dto.request.GroupExpenseUserRequest;
import com.rebalance.dto.response.GroupExpenseResponse;
import com.rebalance.dto.response.GroupExpenseUserResponse;
import com.rebalance.entities.Expense;
import com.rebalance.entities.ExpenseUsers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ExpenseMapper {
    @Mapping(target = "userId", source = "user.id")
    GroupExpenseUserResponse expenseUserToGroupExpenseResponse(ExpenseUsers user);

    @Mapping(target = "users", source = "expenseUsers")
    GroupExpenseResponse expenseToGroupResponse(Expense expense);


    @Mapping(target = "user.id", source = "userId")
    ExpenseUsers groupExpenseUserRequestToExpenseUser(GroupExpenseUserRequest request);

    List<ExpenseUsers> groupExpenseUserRequestListToExpenseUserList(List<GroupExpenseUserRequest> request);


    @Mapping(target = "user.id", source = "initiatorUserId")
    @Mapping(target = "group.id", source = "groupId")
    Expense groupExpenseAddRequestToExpense(GroupExpenseAddRequest request);

    @Mapping(target = "id", source = "expenseId")
    @Mapping(target = "user.id", source = "initiatorUserId")
    @Mapping(target = "group.id", source = "groupId")
    Expense groupExpenseEditRequestToExpense(GroupExpenseEditRequest request);
}
