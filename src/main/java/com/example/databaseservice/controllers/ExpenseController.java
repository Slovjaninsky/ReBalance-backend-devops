package com.example.databaseservice.controllers;

import com.example.databaseservice.entities.ApplicationUser;
import com.example.databaseservice.entities.Expense;
import com.example.databaseservice.exceptions.ExpenseNotFoundException;
import com.example.databaseservice.exceptions.GlobalIdTakenException;
import com.example.databaseservice.exceptions.GroupNotFoundException;
import com.example.databaseservice.exceptions.InvalidRequestException;
import com.example.databaseservice.exceptions.UserNotFoundException;
import com.example.databaseservice.servises.ExpenseService;
import com.example.databaseservice.servises.ApplicationUserService;
import com.example.databaseservice.entities.ExpenseGroup;
import com.example.databaseservice.servises.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ApplicationUserService applicationUserService;
    private final GroupService groupService;

    @Autowired
    public ExpenseController(ExpenseService expenseService, ApplicationUserService applicationUserService, GroupService groupService) {
        this.expenseService = expenseService;
        this.applicationUserService = applicationUserService;
        this.groupService = groupService;
    }

    @PostMapping("/expenses/user/{userId}/group/{groupId}")
    public ResponseEntity<Expense> addExpense(@PathVariable(value = "userId") Long userId, @PathVariable(value = "groupId") Long groupId, @RequestBody Expense inputExpense) {
        ApplicationUser user = applicationUserService.getUserById(userId).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + userId));
        ExpenseGroup group = groupService.getGroupById(groupId).orElseThrow(() -> new GroupNotFoundException("Not found Group with id = " + groupId));
        if(inputExpense.getDescription() == null || inputExpense.getAmount() == null || inputExpense.getGlobalId() == null){
            throw new InvalidRequestException("Request body should contain amount, description and globalId fields");
        }
        if(expenseService.getExpensesByGlobalId(inputExpense.getGlobalId()).size() > 0){
            throw new GlobalIdTakenException("Expenses exist with globalId = " + inputExpense.getGlobalId());
        }
        Expense expense = expenseService.saveExpense(
                new Expense(
                        inputExpense.getAmount(),
                        inputExpense.getDescription(),
                        inputExpense.getGlobalId(),
                        user,
                        group
                )
        );

        return new ResponseEntity<>(expense, HttpStatus.CREATED);
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        expenseService.findAllExpenses().forEach(expenses::add);
        if (expenses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }


    @GetMapping("/expenses/{globalId}")
    public ResponseEntity<List<Expense>> getExpenseById(@PathVariable(value = "globalId") Long id) {
        List<Expense> expenses = expenseService.getExpensesByGlobalId(id);
        if (expenses.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(expenses, HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/expenses")
    public ResponseEntity<List<Expense>> getExpensesFromGroup(@PathVariable(value = "groupId") Long id) {
        ExpenseGroup group = groupService.getGroupById(id).orElseThrow(() -> new GroupNotFoundException("Not found Group with id = " + id));
        return new ResponseEntity(group.getExpenses(), HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/users/{userId}/expenses")
    public ResponseEntity<List<Expense>> getExpensesFromGroup(@PathVariable(value = "groupId") Long groupId, @PathVariable(value = "userId") Long userId) {
        ExpenseGroup group = groupService.getGroupById(groupId).orElseThrow(() -> new GroupNotFoundException("Not found Group with id = " + groupId));
        Set<ApplicationUser> groupUsers = group.getUsers();
        ApplicationUser sample = new ApplicationUser(userId);
        if (groupUsers.contains(sample)) {
            return new ResponseEntity(
                    applicationUserService.getUserById(userId)
                            .get()
                            .getExpenses()
                            .stream()
                            .filter(expense -> expense.getGroup().getId().equals(groupId)),
                    HttpStatus.OK
            );
        }
        throw new UserNotFoundException("Not found User with id = " + userId + " in Group with id = " + groupId);
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<List<Expense>> updateExpensesByGlobalId(@PathVariable(value = "id") Long id, @RequestBody Expense inputExpense) {
        List<Expense> expenses = expenseService.getExpensesByGlobalId(id);
        if (expenses.isEmpty()) {
            throw new ExpenseNotFoundException(String.format("Expenses with globalId = %d not found", id));
        }
        if(inputExpense.getDescription() == null){
            throw new InvalidRequestException("Request body should have \"description\" field");
        }
        expenses.stream().forEach(expense -> expense.setDescription(inputExpense.getDescription()));
        expenses.stream().forEach(expense -> expenseService.saveExpense(expense));
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @DeleteMapping("/expenses/{globalId}")
    public ResponseEntity<HttpStatus> deleteExpenseByGlobalId(@PathVariable("globalId") long id) {
        List<Expense> expenses = expenseService.getExpensesByGlobalId(id);
        if (expenses.isEmpty()) {
            throw new ExpenseNotFoundException(String.format("Expenses with globalId = %d not found", id));
        }
        expenseService.deleteByGlobalId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
