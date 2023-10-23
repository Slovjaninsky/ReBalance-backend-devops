package com.rebalance.controllers;

import com.rebalance.dto.request.GroupExpenseAddRequest;
import com.rebalance.dto.request.GroupExpenseEditRequest;
import com.rebalance.dto.response.GroupExpenseResponse;
import com.rebalance.mapper.ExpenseMapper;
import com.rebalance.servises.ExpenseService;
import com.rebalance.servises.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupExpenseController {
    private final ExpenseService expenseService;
    private final ImageService imageService;
    private final ExpenseMapper expenseMapper;

    //TODO: add pagination
    @GetMapping("/{groupId}/expenses")
    public ResponseEntity<List<GroupExpenseResponse>> getExpensesOfGroup(@PathVariable(value = "groupId") Long groupId) {
        return new ResponseEntity<>(
                expenseService.getExpensesOfGroup(groupId).stream()
                        .map(expenseMapper::expenseToGroupResponse).toList(),
                HttpStatus.OK);
    }

    @PostMapping("/expenses")
    public ResponseEntity<GroupExpenseResponse> addExpense(@RequestBody GroupExpenseAddRequest request) {
        return new ResponseEntity<>(
                expenseMapper.expenseToGroupResponse(
                        expenseService.saveGroupExpense(
                                expenseMapper.groupExpenseAddRequestToExpense(request),
                                expenseMapper.groupExpenseUserRequestListToExpenseUserList(request.getUsers()))),
                HttpStatus.OK);
    }

    @PutMapping("/expenses")
    public ResponseEntity<GroupExpenseResponse> editExpense(@RequestBody GroupExpenseEditRequest request) {
        return new ResponseEntity<>(
                expenseMapper.expenseToGroupResponse(
                        expenseService.editGroupExpense(
                                expenseMapper.groupExpenseEditRequestToExpense(request),
                                expenseMapper.groupExpenseUserRequestListToExpenseUserList(request.getUsers()))),
                HttpStatus.OK);
    }

    @DeleteMapping("/expenses/{expenseId}")
    public ResponseEntity<HttpStatus> deleteExpenseById(@PathVariable("expenseId") Long expenseId) {
        expenseService.deleteGroupExpenseById(expenseId);

        try {
            imageService.deleteImageByGlobalId(expenseId);
        } catch (RuntimeException ignored) {
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
