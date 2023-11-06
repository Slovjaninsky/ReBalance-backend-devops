package com.rebalance.controller;

import com.rebalance.dto.request.GroupExpenseAddRequest;
import com.rebalance.dto.request.GroupExpenseEditRequest;
import com.rebalance.dto.response.GroupExpenseResponse;
import com.rebalance.mapper.ExpenseMapper;
import com.rebalance.service.ExpenseService;
import com.rebalance.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Group expenses management")
@RestController
@RequestMapping(APIVersion.current + "/group")
@RequiredArgsConstructor
public class GroupExpenseController {
    private final ExpenseService expenseService;
    private final ImageService imageService;
    private final ExpenseMapper expenseMapper;

    @Operation(summary = "Get expenses of group")
    @GetMapping("/{groupId}/expenses")
    public ResponseEntity<List<GroupExpenseResponse>> getExpensesOfGroup(@PathVariable(value = "groupId") Long groupId) {
        return new ResponseEntity<>(
                expenseService.getExpensesOfGroup(groupId).stream()
                        .map(expenseMapper::expenseToGroupResponse).toList(),
                HttpStatus.OK);
    }

    @Operation(summary = "Add expense to group")
    @PostMapping("/expenses")
    public ResponseEntity<GroupExpenseResponse> addExpense(@RequestBody @Validated GroupExpenseAddRequest request) {
        return new ResponseEntity<>(
                expenseMapper.expenseToGroupResponse(
                        expenseService.saveGroupExpense(
                                expenseMapper.groupExpenseAddRequestToExpense(request),
                                expenseMapper.groupExpenseUserRequestListToExpenseUserList(request.getUsers()))),
                HttpStatus.OK);
    }

    @Operation(summary = "Edit existing expense of group")
    @PutMapping("/expenses")
    public ResponseEntity<GroupExpenseResponse> editExpense(@RequestBody @Validated GroupExpenseEditRequest request) {
        return new ResponseEntity<>(
                expenseMapper.expenseToGroupResponse(
                        expenseService.editGroupExpense(
                                expenseMapper.groupExpenseEditRequestToExpense(request),
                                expenseMapper.groupExpenseUserRequestListToExpenseUserList(request.getUsers()))),
                HttpStatus.OK);
    }

    @Operation(summary = "Delete expense from group")
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
