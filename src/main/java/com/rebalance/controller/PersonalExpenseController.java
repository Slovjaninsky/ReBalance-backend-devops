package com.rebalance.controller;

import com.rebalance.dto.request.PersonalExpenseAddRequest;
import com.rebalance.dto.request.PersonalExpenseEditRequest;
import com.rebalance.dto.response.PersonalExpenseResponse;
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

@Tag(name = "Personal expenses management")
@RestController
@RequestMapping(APIVersion.current + "/personal/expenses")
@RequiredArgsConstructor
public class PersonalExpenseController {
    private final ExpenseService expenseService;
    private final ImageService imageService;
    private final ExpenseMapper expenseMapper;

    @Operation(summary = "Get personal expenses")
    @GetMapping()
    public ResponseEntity<List<PersonalExpenseResponse>> getPersonalExpenses() {
        return new ResponseEntity<>(
                expenseService.getExpensesOfUser().stream()
                        .map(expenseMapper::expenseToPersonalResponse).toList(),
                HttpStatus.OK);
    }

    @Operation(summary = "Add personal expense")
    @PostMapping()
    public ResponseEntity<PersonalExpenseResponse> addExpense(@RequestBody @Validated PersonalExpenseAddRequest request) {
        return new ResponseEntity<>(
                expenseMapper.expenseToPersonalResponse(
                        expenseService.savePersonalExpense(
                                expenseMapper.personalExpenseAddRequestToExpense(request))),
                HttpStatus.OK);
    }

    @Operation(summary = "Edit existing personal expense")
    @PutMapping()
    public ResponseEntity<PersonalExpenseResponse> editExpense(@RequestBody @Validated PersonalExpenseEditRequest request) {
        return new ResponseEntity<>(
                expenseMapper.expenseToPersonalResponse(
                        expenseService.editPersonalExpense(
                                expenseMapper.personalExpenseEditRequestToExpense(request))),
                HttpStatus.OK);
    }

    @Operation(summary = "Delete personal expense")
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<HttpStatus> deleteExpenseById(@PathVariable("expenseId") Long expenseId) {
        expenseService.deletePersonalExpenseById(expenseId);

        try {
            imageService.deleteImageByGlobalId(expenseId);
        } catch (RuntimeException ignored) {
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
