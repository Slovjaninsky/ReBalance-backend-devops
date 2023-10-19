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
import java.time.format.DateTimeFormatter;
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
        if (expenseUsers.stream().mapToDouble(ExpenseUsers::getAmount).sum() != expense.getAmount()) {
            throw new RebalanceException(RebalanceErrorType.RB_104);
        }

        User initiator = userService.getUserById(expense.getUser().getId());
        Group group = groupService.getGroupById(expense.getGroup().getId());

        groupService.validateUsersInGroup(expenseUsers.stream().map(expenseUser ->
                expenseUser.getUser().getId()).toList(), group.getId());

        expense.setDate(LocalDate.now());
        expenseRepository.save(expense);

        expenseUsers.forEach(u -> u.setExpense(expense));
        expenseUsersRepository.saveAll(expenseUsers);
        expense.setExpenseUsers(new HashSet<>(expenseUsers));

        return expense;
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_101));
    }

    public void deleteById(Long expenseId) {
        getExpenseById(expenseId);
        expenseRepository.deleteById(expenseId);
    }

    public void throwExceptionIfExpensesWithGlobalIdNotFound(Long globalId) {
        if (expenseRepository.findById(globalId).isEmpty()) {
            throw new RebalanceException(RebalanceErrorType.RB_101);
        }
    }

    public List<Expense> getExpensesByGroupIdAndBetweenDates(Long groupId, String firstDateString, String secondDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate firstDate = LocalDate.parse(firstDateString, formatter);
        LocalDate secondDate = LocalDate.parse(secondDateString, formatter);
        if (secondDate.isBefore(firstDate)) {
            throw new RebalanceException(RebalanceErrorType.RB_102);
        }
//        groupService.throwExceptionIfGroupNotFoundById(groupId);
        return expenseRepository.findByGroupIdAndDateBetween(groupId, firstDate, secondDate);
    }

    public List<Expense> getExpensesByGroupAndFromDateByTimePeriod(Long groupId, String firstDateString, String period) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate firstDate = LocalDate.parse(firstDateString, formatter);
        groupService.getGroupById(groupId);
        LocalDate secondDate;
        switch (period.toLowerCase()) {
            case "year":
                secondDate = firstDate.plusYears(1).minusDays(1);
                break;
            case "month":
                secondDate = firstDate.plusMonths(1).minusDays(1);
                break;
            case "week":
                secondDate = firstDate.plusWeeks(1).minusDays(1);
                break;
            case "day":
                secondDate = firstDate;
                break;
            default:
                throw new RebalanceException(RebalanceErrorType.RB_103);
        }
//        groupService.throwExceptionIfGroupNotFoundById(groupId);
        return expenseRepository.findByGroupIdAndDateBetween(groupId, firstDate, secondDate);
    }

    public List<Expense> getExpensesOfGroup(Long groupId) {
        Group group = groupService.getGroupByIdWithExpenses(groupId);
        return group.getExpenses().stream().toList();
    }
}
