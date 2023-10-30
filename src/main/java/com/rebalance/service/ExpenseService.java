package com.rebalance.service;

import com.rebalance.entity.Expense;
import com.rebalance.entity.ExpenseUsers;
import com.rebalance.entity.Group;
import com.rebalance.entity.User;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.ExpenseRepository;
import com.rebalance.repository.ExpenseUsersRepository;
import com.rebalance.security.SignedInUsernameGetter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ExpenseUsersRepository expenseUsersRepository;
    private final SignedInUsernameGetter signedInUsernameGetter;

    public Expense saveGroupExpense(Expense expense, List<ExpenseUsers> expenseUsers) {
        // validate input data
        validateUsersAmount(expense.getAmount(), expenseUsers);
        groupService.validateGroupExistsAndNotPersonal(expense.getGroup().getId());

        // validate users and initiator in group
        validateUsersInGroup(expenseUsers, expense.getInitiator(), expense.getGroup().getId());

        // set auto generated fields
        expense.setAddedBy(signedInUsernameGetter.getUser());
        expense.setDate(LocalDate.now());
        expenseRepository.save(expense);

        // save participants of expense
        expenseUsers.forEach(u -> u.setExpense(expense));
        expenseUsersRepository.saveAll(expenseUsers);

        // set expense participants for response
        expense.setExpenseUsers(new HashSet<>(expenseUsers));
        return expense;
    }

    @Transactional
    public Expense editGroupExpense(Expense expenseRequest, List<ExpenseUsers> expenseUsers) {
        // get existing expense
        Expense expense = getExpenseById(expenseRequest.getId());

        // validate input data
        validateUsersAmount(expenseRequest.getAmount(), expenseUsers);
        groupService.validateGroupIsNotPersonal(expense.getGroup());

        // validate users and initiator in group
        validateUsersInGroup(expenseUsers, expense.getInitiator(), expense.getGroup().getId());

        // update fields
        expense.setAmount(expenseRequest.getAmount());
        expense.setDescription(expenseRequest.getDescription());
        expense.setCategory(expenseRequest.getCategory());
        expenseRepository.save(expense);

        // update users
        expenseUsersRepository.deleteAllByExpenseId(expense.getId());
        expenseUsers.forEach(u -> u.setExpense(expense));
        expenseUsersRepository.saveAll(expenseUsers);
        expense.setExpenseUsers(new HashSet<>(expenseUsers));

        return expense;
    }

    public Expense savePersonalExpense(Expense expense) {
        User signedInUser = signedInUsernameGetter.getUser();
        Group group = groupService.getPersonalGroupByUserId(signedInUser.getId());

        expense.setInitiator(signedInUser);
        expense.setAddedBy(signedInUser);
        expense.setGroup(group);
        expense.setDate(LocalDate.now());
        return expenseRepository.save(expense);
    }

    public Expense editPersonalExpense(Expense expenseRequest) {
        // get existing expense
        Expense expense = getExpenseById(expenseRequest.getId());

        User signedInUser = signedInUsernameGetter.getUser();

        // validate expense is user's personal
        if (!expense.getGroup().isPersonalOf(signedInUser)) {
            throw new RebalanceException(RebalanceErrorType.RB_204);
        }

        // update fields
        expense.setAmount(expenseRequest.getAmount());
        expense.setDescription(expenseRequest.getDescription());
        expense.setCategory(expenseRequest.getCategory());
        return expenseRepository.save(expense);
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_101));
    }

    public void deleteGroupExpenseById(Long expenseId) {
        Expense expense = getExpenseById(expenseId);
        groupService.validateGroupIsNotPersonal(expense.getGroup());

        expenseRepository.deleteById(expenseId);
    }

    public void deletePersonalExpenseById(Long expenseId) {
        Expense expense = getExpenseById(expenseId);
        User signedInUser = signedInUsernameGetter.getUser();
        groupService.validateGroupIsPersonal(expense.getGroup().getId(), signedInUser.getId());

        expenseRepository.deleteById(expenseId);
    }

    public void throwExceptionIfExpensesWithGlobalIdNotFound(Long globalId) {
        if (expenseRepository.findById(globalId).isEmpty()) {
            throw new RebalanceException(RebalanceErrorType.RB_101);
        }
    }

    public List<Expense> getExpensesOfGroup(Long groupId) {
        groupService.validateGroupExistsAndNotPersonal(groupId);
        return expenseRepository.findAllByGroupId(groupId);
    }

    public List<Expense> getExpensesOfUser() {
        User signedInuser = signedInUsernameGetter.getUser();
        Group personalGroup = groupService.getPersonalGroupByUserId(signedInuser.getId());
        return expenseRepository.findAllByGroupId(personalGroup.getId());
    }

    private void validateUsersAmount(Double amount, List<ExpenseUsers> expenseUsers) {
        if (expenseUsers.stream().mapToDouble(ExpenseUsers::getAmount).sum() != amount) {
            throw new RebalanceException(RebalanceErrorType.RB_104);
        }
    }

    private void validateUsersInGroup(List<ExpenseUsers> users, User initiator, Long groupId) {
        Set<Long> userIds = users.stream().map(expenseUser ->
                expenseUser.getUser().getId()).collect(Collectors.toSet());
        userIds.add(initiator.getId());
        groupService.validateUsersInGroup(userIds, groupId);
    }
}
