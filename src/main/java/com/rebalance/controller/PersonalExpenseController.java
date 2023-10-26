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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personal")
@RequiredArgsConstructor
public class PersonalExpenseController {
    private final ExpenseService expenseService;
    private final ImageService imageService;
    private final ExpenseMapper expenseMapper;

    //TODO: add pagination
    @GetMapping("/{userId}/expenses")
    public ResponseEntity<List<PersonalExpenseResponse>> getPersonalExpenses(@PathVariable(value = "userId") Long userId) {
        return new ResponseEntity<>(
                expenseService.getExpensesOfUser(userId).stream()
                        .map(expenseMapper::expenseToPersonalResponse).toList(),
                HttpStatus.OK);
    }

    @PostMapping("/expenses")
    public ResponseEntity<PersonalExpenseResponse> addExpense(@RequestBody PersonalExpenseAddRequest request) {
        return new ResponseEntity<>(
                expenseMapper.expenseToPersonalResponse(
                        expenseService.savePersonalExpense(
                                expenseMapper.perosnalExpenseAddRequestToExpense(request))),
                HttpStatus.OK);
    }

    @PutMapping("/expenses")
    public ResponseEntity<PersonalExpenseResponse> editExpense(@RequestBody PersonalExpenseEditRequest request) {
        return new ResponseEntity<>(
                expenseMapper.expenseToPersonalResponse(
                        expenseService.editPersonalExpense(
                                expenseMapper.personalExpenseEditRequestToExpense(request))),
                HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/expenses/{expenseId}")
    public ResponseEntity<HttpStatus> deleteExpenseById(@PathVariable("userId") Long userId, @PathVariable("expenseId") Long expenseId) {
        expenseService.deletePersonalExpenseById(userId, expenseId);

        try {
            imageService.deleteImageByGlobalId(expenseId);
        } catch (RuntimeException ignored) {
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
