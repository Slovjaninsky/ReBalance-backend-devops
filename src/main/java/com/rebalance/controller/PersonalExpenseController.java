package com.rebalance.controller;

import com.rebalance.dto.request.PersonalExpenseAddRequest;
import com.rebalance.dto.request.PersonalExpenseEditRequest;
import com.rebalance.dto.response.PersonalExpenseResponse;
import com.rebalance.mapper.ExpenseMapper;
import com.rebalance.service.ExpenseService;
import com.rebalance.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personal/expenses")
@RequiredArgsConstructor
public class PersonalExpenseController {
    private final ExpenseService expenseService;
    private final ImageService imageService;
    private final ExpenseMapper expenseMapper;

    @GetMapping()
    public ResponseEntity<List<PersonalExpenseResponse>> getPersonalExpenses() {
        return new ResponseEntity<>(
                expenseService.getExpensesOfUser().stream()
                        .map(expenseMapper::expenseToPersonalResponse).toList(),
                HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<PersonalExpenseResponse> addExpense(@RequestBody @Validated PersonalExpenseAddRequest request) {
        return new ResponseEntity<>(
                expenseMapper.expenseToPersonalResponse(
                        expenseService.savePersonalExpense(
                                expenseMapper.personalExpenseAddRequestToExpense(request))),
                HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<PersonalExpenseResponse> editExpense(@RequestBody @Validated PersonalExpenseEditRequest request) {
        return new ResponseEntity<>(
                expenseMapper.expenseToPersonalResponse(
                        expenseService.editPersonalExpense(
                                expenseMapper.personalExpenseEditRequestToExpense(request))),
                HttpStatus.OK);
    }

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
