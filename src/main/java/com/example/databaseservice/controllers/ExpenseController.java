package com.example.databaseservice.controllers;

import com.example.databaseservice.entities.Expense;
import com.example.databaseservice.exceptions.*;
import com.example.databaseservice.servises.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/expenses/user/{userId}/group/{groupId}/{userFromId}")
    public ResponseEntity<Expense> addExpense(
            @PathVariable(value = "userId") Long userId,
            @PathVariable(value = "userFromId") Long userFromId,
            @PathVariable(value = "groupId") Long groupId,
            @RequestBody Expense inputExpense) {

        if (inputExpense.getDescription() == null || inputExpense.getAmount() == null || inputExpense.getCategory() == null) {
            throw new InvalidRequestException("Request body should contain amount, description and category fields");
        }
        return new ResponseEntity<>(expenseService.saveExpense(userId, userFromId, groupId, inputExpense), HttpStatus.CREATED);
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        List<Expense> expenses = expenseService.findAllExpenses();
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @GetMapping("/expenses/{expenseId}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable(value = "expenseId") Long id) {
        Expense expenses = expenseService.getExpenseById(id);
        return new ResponseEntity(expenses, HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/expenses")
    public ResponseEntity<Set<Expense>> getExpensesFromGroup(@PathVariable(value = "groupId") Long id) {
        return new ResponseEntity(expenseService.getExpensesFromGroup(id), HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/users/{userId}/expenses")
    public ResponseEntity<List<Expense>> getExpensesOfUserFromGroup(@PathVariable(value = "groupId") Long groupId, @PathVariable(value = "userId") Long userId) {
        List<Expense> expenses = expenseService.getExpensesOfUserFromGroup(groupId, userId);
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<List<Expense>> updateExpensesByGlobalId(@PathVariable(value = "id") Long id, @RequestBody Expense inputExpense) {
        if (inputExpense.getDescription() == null) {
            throw new InvalidRequestException("Request body should have \"description\" field");
        }
        return new ResponseEntity<>(expenseService.updateExpensesByGlobalId(id, inputExpense), HttpStatus.OK);
    }

    @DeleteMapping("/expenses/{globalId}")
    public ResponseEntity<HttpStatus> deleteExpenseByGlobalId(@PathVariable("globalId") long id) {
        expenseService.deleteByGlobalId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/expenses/group/{groupId}/between/{dateFirst}/{dateSecond}")
    public ResponseEntity<List<Expense>> getExpensesByGroupAndBetweenDates(@PathVariable("groupId") Long groupId, @PathVariable("dateFirst") String firstDateString, @PathVariable("dateSecond") String secondDateString) {
        return new ResponseEntity<>(expenseService.getExpensesByGroupIdAndBetweenDates(groupId, firstDateString, secondDateString), HttpStatus.OK);
    }

    @GetMapping("/expenses/group/{groupId}/from/{dateFirst}/{period}")
    public ResponseEntity<List<Expense>> getExpensesByGroupAndFromDateByTimePeriod(@PathVariable("groupId") Long groupId, @PathVariable("dateFirst") String firstDateString, @PathVariable("period") String period) {
        return new ResponseEntity<>(expenseService.getExpensesByGroupAndFromDateByTimePeriod(groupId, firstDateString, period), HttpStatus.OK);
    }

    @GetMapping("/expenses/group/{groupId}/dates")
    public ResponseEntity<Set<String>> getAllDatesOfExpenses(@PathVariable("groupId") Long groupId) {
        return new ResponseEntity<>(expenseService.getAllExpenseDatesFromGroup(groupId), HttpStatus.OK);
    }

}
