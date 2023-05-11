package com.example.databaseservice.servises;

import com.example.databaseservice.entities.ApplicationUser;
import com.example.databaseservice.entities.Expense;
import com.example.databaseservice.entities.ExpenseGroup;
import com.example.databaseservice.entities.Notification;
import com.example.databaseservice.exceptions.ExpenseNotFoundException;
import com.example.databaseservice.exceptions.FirstDateMustBeBeforeSecondDateException;
import com.example.databaseservice.exceptions.IncorrectTimePeriodException;
import com.example.databaseservice.exceptions.UserNotFoundException;
import com.example.databaseservice.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final ApplicationUserService applicationUserService;
    private final NotificationService notificationService;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, GroupService groupService, ApplicationUserService applicationUserService, NotificationService notificationService) {
        this.expenseRepository = expenseRepository;
        this.groupService = groupService;
        this.applicationUserService = applicationUserService;
        this.notificationService = notificationService;
    }

    public List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense saveExpense(Long userId, Long userFromId, Long groupId, Expense inputExpense) {

        ApplicationUser user = applicationUserService.getUserById(userId);
        ExpenseGroup group = groupService.getGroupById(groupId);
        Expense expense =
                new Expense(
                        inputExpense.getAmount(),
                        inputExpense.getDescription(),
                        inputExpense.getCategory(),
                        user,
                        group
                );
        if (inputExpense.getDateStamp() != null) {
            expense.setDateStamp(inputExpense.getDateStamp());
        } else {
            expense.setDateStamp(LocalDate.now());
        }
        expenseRepository.save(expense);

        if (inputExpense.getGlobalId() != null) {
            expense.setGlobalId(inputExpense.getGlobalId());
        } else {
            expense.setGlobalId(expenseRepository.getMaxGlobalId() + 1);
        }

        if (expense.getGlobalId() > 0) {
            notificationService.saveNotification(new Notification(expense.getUser().getId(), userFromId, expense.getId(), expense.getAmount(), true));
        }

        return expenseRepository.save(expense);
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> new ExpenseNotFoundException(String.format("Expenses with globalId = %d not found", id)));
    }

    public void deleteByGlobalId(Long globalId) {
        List<Expense> expenses = expenseRepository.findByGlobalId(globalId);
        if (expenses.isEmpty()) {
            throw new ExpenseNotFoundException(String.format("Expenses with globalId = %d not found", globalId));
        }
        expenseRepository.deleteByGlobalId(globalId);
    }

    public List<Expense> getExpensesByGlobalId(Long globalId) {
        return expenseRepository.findByGlobalId(globalId);
    }

    public void throwExceptionIfExpensesWithGlobalIdNotFound(Long globalId) {
        if (expenseRepository.findByGlobalId(globalId).isEmpty()) {
            throw new ExpenseNotFoundException(String.format("Expenses with globalId = %d not found", globalId));
        }
    }

    public List<Expense> getExpensesByGroupIdAndBetweenDates(Long groupId, String firstDateString, String secondDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate firstDate = LocalDate.parse(firstDateString, formatter);
        LocalDate secondDate = LocalDate.parse(secondDateString, formatter);
        if (secondDate.isBefore(firstDate)) {
            throw new FirstDateMustBeBeforeSecondDateException("First date (" + firstDate + ") must be before or at the second date (" + secondDate + ")");
        }
        groupService.throwExceptionIfGroupNotFoundById(groupId);
        return expenseRepository.findByGroupIdAndDateStampBetween(groupId, firstDate, secondDate);
    }

    public List<Expense> getExpensesByGroupAndFromDateByTimePeriod(Long groupId, String firstDateString, String period){
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
                throw new IncorrectTimePeriodException();
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
        throw new UserNotFoundException("Not found User with id = " + userId + " in Group with id = " + groupId);
    }

    public Set<Expense> getExpensesFromGroup(Long groupId) {
        ExpenseGroup group = groupService.getGroupById(groupId);
        return group.getExpenses();
    }

    public List<Expense> updateExpensesByGlobalId(Long globalId, Expense inputExpense){
        List<Expense> expenses = expenseRepository.findByGlobalId(globalId);
        if (expenses.isEmpty()) {
            throw new ExpenseNotFoundException(String.format("Expenses with globalId = %d not found", globalId));
        }
        expenses.stream().forEach(expense -> expense.setDescription(inputExpense.getDescription()));
        expenses.stream().forEach(expense -> expenseRepository.save(expense));
        return expenses;
    }

}
