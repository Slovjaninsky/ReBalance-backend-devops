package com.rebalance.servises;

import com.rebalance.dto.request.GroupExpenseAddRequest;
import com.rebalance.entities.Expense;
import com.rebalance.entities.Group;
import com.rebalance.entities.Notification;
import com.rebalance.entities.User;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, GroupService groupService, UserService userService, NotificationService notificationService) {
        this.expenseRepository = expenseRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense saveExpense(GroupExpenseAddRequest request) {
        //TODO: reimplement
        return null;
    }

    public Expense saveExpense(Long userId, Long userFromId, Long groupId, Expense inputExpense) {

        User user = userService.getUserById(userId);
        Group group = groupService.getGroupById(groupId);
        Expense expense = new Expense();
        expense.setAmount(inputExpense.getAmount());
        expense.setDescription(inputExpense.getDescription());
        expense.setCategory(inputExpense.getCategory());
        expense.setUser(user);
        expense.setGroup(group);
        if (inputExpense.getDate() != null) {
            expense.setDate(inputExpense.getDate());
        } else {
            expense.setDate(LocalDate.now());
        }
        expenseRepository.save(expense);

        if (inputExpense.getGlobalId() != null) {
            expense.setGlobalId(inputExpense.getGlobalId());
        } else {
            Long maxGlobalId = expenseRepository.getMaxGlobalId() == null ? 1 : expenseRepository.getMaxGlobalId();
            expense.setGlobalId(maxGlobalId + 1);
        }

        if (expense.getGlobalId() > 0) {
            notificationService.saveNotification(new Notification(expense.getUser().getId(), userFromId, expense.getId(), expense.getAmount(), true));
        }

        return expenseRepository.save(expense);
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_101));
    }

    public void deleteByGlobalId(Long globalId) {
        List<Expense> expenses = expenseRepository.findByGlobalId(globalId);
        if (expenses.isEmpty()) {
            throw new RebalanceException(RebalanceErrorType.RB_101);
        }
        expenseRepository.deleteByGlobalId(globalId);
    }

    public List<Expense> getExpensesByGlobalId(Long globalId) {
        return expenseRepository.findByGlobalId(globalId);
    }

    public void throwExceptionIfExpensesWithGlobalIdNotFound(Long globalId) {
        if (expenseRepository.findByGlobalId(globalId).isEmpty()) {
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
        groupService.throwExceptionIfGroupNotFoundById(groupId);
        return expenseRepository.findByGroupIdAndDateStampBetween(groupId, firstDate, secondDate);
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
        groupService.throwExceptionIfGroupNotFoundById(groupId);
        return expenseRepository.findByGroupIdAndDateStampBetween(groupId, firstDate, secondDate);
    }

    public Set<String> getAllExpenseDatesFromGroup(Long groupId) {
        groupService.throwExceptionIfGroupNotFoundById(groupId);
        return expenseRepository.findAllDateStampsByGroup(groupId)
                .stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .collect(Collectors.toSet());
    }

    public Long getMaxGlobalId() {
        return expenseRepository.getMaxGlobalId();
    }

    public List<Expense> getExpensesOfUserFromGroup(Long groupId, Long userId) {
        ExpenseGroup group = groupService.getGroupById(groupId);
        Set<ApplicationUser> groupUsers = group.getUsers();
        ApplicationUser sample = new ApplicationUser(userId);
        if (groupUsers.contains(sample)) {
            return applicationUserService.getUserById(userId)
                    .getExpenses()
                    .stream()
                    .filter(expense -> expense.getGroup().getId().equals(groupId))
                    .toList();
        }
        throw new RebalanceException(RebalanceErrorType.RB_101);
    }

    public Set<Expense> getExpensesFromGroup(Long groupId) {
        Group group = groupService.getGroupById(groupId);
        return group.getExpenses();
    }

    public List<Expense> updateExpensesByGlobalId(Long globalId, Expense inputExpense) {
        List<Expense> expenses = expenseRepository.findByGlobalId(globalId);
        if (expenses.isEmpty()) {
            throw new RebalanceException(RebalanceErrorType.RB_101);
        }
        expenses.stream().forEach(expense -> expense.setDescription(inputExpense.getDescription()));
        expenses.stream().forEach(expense -> expenseRepository.save(expense));
        return expenses;
    }
}
