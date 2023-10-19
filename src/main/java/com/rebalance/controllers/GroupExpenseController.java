package com.rebalance.controllers;

import com.rebalance.dto.request.GroupExpenseAddRequest;
import com.rebalance.dto.response.ExpenseResponse;
import com.rebalance.mapper.ExpenseMapper;
import com.rebalance.servises.ExpenseService;
import com.rebalance.servises.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense/group")
@RequiredArgsConstructor
public class GroupExpenseController {
    private final ExpenseService expenseService;
    private final ImageService imageService;
    private final ExpenseMapper expenseMapper;

    //TODO: add pagination
    @GetMapping("/{groupId}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesOfGroup(@PathVariable(value = "groupId") Long groupId) {
        return new ResponseEntity<>(
                expenseService.getExpensesOfGroup(groupId).stream()
                        .map(expenseMapper::expenseToResponse).toList(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<String> addExpense(@RequestBody GroupExpenseAddRequest request) {
        expenseService.saveGroupExpense(expenseMapper.groupExpenseRequestToExpense(request),
                expenseMapper.groupExpenseUserRequestToExpenseUserList(request.getUsers()));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<HttpStatus> deleteExpenseByGlobalId(@PathVariable("expenseId") Long expenseId) {
        expenseService.deleteByGlobalId(expenseId);
        try {
            imageService.deleteImageByGlobalId(expenseId);
        } catch (RuntimeException ignored) {
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
