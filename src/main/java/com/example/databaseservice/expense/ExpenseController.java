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

    @GetMapping(path = "expense/all")
    public List<String> getAllExpensesAsString() {
        return expenseService.findAllExpenses().stream().map(val -> val.toString()).collect(Collectors.toList());
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


    @GetMapping("/expenses/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable(value = "id") Long id) {
        Optional<Expense> expenseOptional = expenseService.getExpenseById(id);

        if (expenseOptional.isEmpty()) {
            return new ResponseEntity(null, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(expenseOptional.get(), HttpStatus.OK);
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
    public ResponseEntity<Expense> updateExpense(@PathVariable(value = "id") Long id, @RequestBody Expense inputExpense) {
        Optional<Expense> expenseOptional = expenseService.getExpenseById(id);

        if (expenseOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Expense expense = expenseOptional.get();

        expense.setDescription(inputExpense.getDescription());

        return new ResponseEntity(expenseService.saveExpense(expense), HttpStatus.OK);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<HttpStatus> deleteExpense(@PathVariable("id") long id) {
        expenseService.deleteExpenseById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
