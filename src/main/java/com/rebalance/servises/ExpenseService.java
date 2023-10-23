package com.rebalance.servises;

import com.rebalance.entities.Expense;
import com.rebalance.entities.ExpenseUsers;
import com.rebalance.entities.Group;
import com.rebalance.entities.User;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repositories.ExpenseRepository;
import com.rebalance.repositories.ExpenseUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ExpenseUsersRepository expenseUsersRepository;

    public Expense saveGroupExpense(Expense expense, List<ExpenseUsers> expenseUsers) {
        validateUsersAmount(expense.getAmount(), expenseUsers);

        User initiator = userService.getUserById(expense.getUser().getId());
        groupService.validateGroupExistsAndNotPersonal(expense.getGroup().getId());

        groupService.validateUsersInGroup(expenseUsers.stream().map(expenseUser ->
                expenseUser.getUser().getId()).toList(), expense.getGroup().getId());

        expense.setDate(LocalDate.now());
        expenseRepository.save(expense);

        expenseUsers.forEach(u -> u.setExpense(expense));
        expenseUsersRepository.saveAll(expenseUsers);
        expense.setExpenseUsers(new HashSet<>(expenseUsers));

        return expense;
    }

    public Expense savePersonalExpense(Expense expense) {
        User initiator = userService.getUserById(expense.getUser().getId());
        Long groupId = expense.getGroup().getId();
        groupService.validateGroupIsPersonal(groupId, initiator.getId());

        expense.setDate(LocalDate.now());
        expenseRepository.save(expense);

        return expense;
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_101));
    }

    public void deleteGroupExpenseById(Long expenseId) {
        Expense expense = getExpenseById(expenseId);
        groupService.validateGroupIsNotPersonal(expense.getGroup());

        expenseRepository.deleteById(expenseId);
    }

    public void deleteById(Long expenseId) {
        validateExpenseExists(expenseId);
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

    public List<Expense> getExpensesOfUser(Long userId) {
        Group personalGroup = groupService.getPersonalGroupByUserId(userId);
        return expenseRepository.findAllByGroupId(personalGroup.getId());
    }

    private void validateUsersAmount(Double amount, List<ExpenseUsers> expenseUsers) {
        if (expenseUsers.stream().mapToDouble(ExpenseUsers::getAmount).sum() != amount) {
            throw new RebalanceException(RebalanceErrorType.RB_104);
        }
    }

    private void validateExpenseExists(Long expenseId) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new RebalanceException(RebalanceErrorType.RB_101);
        }
    }
}
