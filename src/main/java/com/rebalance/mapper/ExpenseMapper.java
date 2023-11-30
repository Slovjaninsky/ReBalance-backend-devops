package com.rebalance.mapper;

import com.rebalance.dto.request.*;
import com.rebalance.dto.response.GroupExpenseResponse;
import com.rebalance.dto.response.GroupExpenseUserResponse;
import com.rebalance.dto.response.PersonalExpenseResponse;
import com.rebalance.entity.Expense;
import com.rebalance.entity.ExpenseUsers;
import com.rebalance.mapper.converter.DecimalConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = DecimalConverter.class)
public interface ExpenseMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "bigDecimalToDouble")
    GroupExpenseUserResponse expenseUserToGroupExpenseResponse(ExpenseUsers user);

    @Mapping(target = "initiatorUserId", source = "initiator.id")
    @Mapping(target = "addedByUserId", source = "addedBy.id")
    @Mapping(target = "users", source = "expenseUsers")
    @Mapping(target = "category", source = "category.category.name")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "bigDecimalToDouble")
    GroupExpenseResponse expenseToGroupResponse(Expense expense);


    @Mapping(target = "category", source = "category.category.name")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "bigDecimalToDouble")
    PersonalExpenseResponse expenseToPersonalResponse(Expense expense);


    @Mapping(target = "user.id", source = "userId")
    ExpenseUsers groupExpenseUserRequestToExpenseUser(GroupExpenseUserRequest request);

    List<ExpenseUsers> groupExpenseUserRequestListToExpenseUserList(List<GroupExpenseUserRequest> request);


    @Mapping(target = "initiator.id", source = "initiatorUserId")
    @Mapping(target = "group.id", source = "groupId")
    @Mapping(target = "category", ignore = true)
    Expense groupExpenseAddRequestToExpense(GroupExpenseAddRequest request);

    @Mapping(target = "category", ignore = true)
    Expense personalExpenseAddRequestToExpense(PersonalExpenseAddRequest request);

    @Mapping(target = "id", source = "expenseId")
    @Mapping(target = "initiator.id", source = "initiatorUserId")
    @Mapping(target = "category", ignore = true)
    Expense groupExpenseEditRequestToExpense(GroupExpenseEditRequest request);

    @Mapping(target = "id", source = "expenseId")
    @Mapping(target = "category", ignore = true)
    Expense personalExpenseEditRequestToExpense(PersonalExpenseEditRequest request);
}
