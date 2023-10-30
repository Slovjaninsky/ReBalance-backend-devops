package com.rebalance.mapper;

import com.rebalance.dto.request.*;
import com.rebalance.dto.response.GroupExpenseResponse;
import com.rebalance.dto.response.GroupExpenseUserResponse;
import com.rebalance.dto.response.PersonalExpenseResponse;
import com.rebalance.entity.Expense;
import com.rebalance.entity.ExpenseUsers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ExpenseMapper {
    @Mapping(target = "userId", source = "user.id")
    GroupExpenseUserResponse expenseUserToGroupExpenseResponse(ExpenseUsers user);

    @Mapping(target = "users", source = "expenseUsers")
    GroupExpenseResponse expenseToGroupResponse(Expense expense);


    PersonalExpenseResponse expenseToPersonalResponse(Expense expense);


    @Mapping(target = "user.id", source = "userId")
    ExpenseUsers groupExpenseUserRequestToExpenseUser(GroupExpenseUserRequest request);

    List<ExpenseUsers> groupExpenseUserRequestListToExpenseUserList(List<GroupExpenseUserRequest> request);


    @Mapping(target = "initiator.id", source = "initiatorUserId")
    @Mapping(target = "group.id", source = "groupId")
    Expense groupExpenseAddRequestToExpense(GroupExpenseAddRequest request);

    @Mapping(target = "initiator.id", source = "initiatorUserId")
    @Mapping(target = "group.id", source = "groupId")
    Expense perosnalExpenseAddRequestToExpense(PersonalExpenseAddRequest request);

    @Mapping(target = "id", source = "expenseId")
    @Mapping(target = "initiator.id", source = "initiatorUserId")
    Expense groupExpenseEditRequestToExpense(GroupExpenseEditRequest request);

    @Mapping(target = "id", source = "expenseId")
    @Mapping(target = "initiator.id", source = "userId")
    Expense personalExpenseEditRequestToExpense(PersonalExpenseEditRequest request);
}
