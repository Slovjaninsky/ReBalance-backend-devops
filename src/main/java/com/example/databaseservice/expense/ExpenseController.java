package com.example.databaseservice.expense;

import com.example.databaseservice.applicationuser.ApplicationUser;
import com.example.databaseservice.applicationuser.ApplicationUserService;
import com.example.databaseservice.group.ExpenseGroup;
import com.example.databaseservice.group.GroupService;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public ResponseEntity<Expense> addExpense(@PathVariable(value = "userId") Long userId, @PathVariable(value = "groupId") Long groupId, @RequestBody Expense inputexpense) {

        ApplicationUser user = applicationUserService.getUserById(userId).orElseThrow(() -> new RuntimeException("Not found User with id = " + userId));
        ExpenseGroup group = groupService.getGroupById(groupId).orElseThrow(() -> new RuntimeException("Not found Group with id = " + groupId));

        Expense expense = expenseService.saveExpense(
                new Expense(
                        inputexpense.getAmount(),
                        inputexpense.getDescription(),
                        inputexpense.getGlobalId(),
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
        Optional<ExpenseGroup> groupOptional = groupService.getGroupById(id);

        if (groupOptional.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(groupOptional.get().getExpenses(), HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/users/{userId}/expenses")
    public ResponseEntity<List<Expense>> getExpensesFromGroup(@PathVariable(value = "groupId") Long groupId, @PathVariable(value = "userId") Long userId) {
        Optional<ExpenseGroup> groupOptional = groupService.getGroupById(groupId);

        if (groupOptional.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }

        Set<ApplicationUser> groupUsers = groupOptional.get().getUsers();

        ApplicationUser sample = new ApplicationUser(userId);

        if (groupUsers.contains(sample)) {
            return new ResponseEntity(
                    applicationUserService.getUserById(userId).get().getExpenses().stream().filter(
                            expense -> expense.getGroup().getId().equals(groupId)
                    ),
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<List<Expense>> updateExpensesByGlobalId(@PathVariable(value = "id") Long id, @RequestBody Expense inputExpense) {
        List<Expense> expenses = expenseService.getExpensesByGlobalId(id);

        if (expenses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        expenses.stream().forEach(expense -> expense.setDescription(inputExpense.getDescription()));

        expenses.stream().forEach(expense -> expenseService.saveExpense(expense));

        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @DeleteMapping("/expenses/{globalId}")
    public ResponseEntity<HttpStatus> deleteExpenseByGlobalId(@PathVariable("globalId") long id) {
        expenseService.deleteByGlobalId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
