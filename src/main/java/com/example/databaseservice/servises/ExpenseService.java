package com.example.databaseservice.servises;

import com.example.databaseservice.entities.Expense;
import com.example.databaseservice.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    public void deleteExpenseById(Long id) {
        expenseRepository.deleteById(id);
    }

    public void deleteByGlobalId(Long globalId) {
        expenseRepository.deleteByGlobalId(globalId);
    }

    public List<Expense> getExpensesByGlobalId(Long globalId) {
        return expenseRepository.findByGlobalId(globalId);
    }

    public List<Expense> getExpensesByGroupIdAndBetweenDates(Long groupId, LocalDate firstDate, LocalDate secondDate) {
        return expenseRepository.findByGroupIdAndDateStampBetween(groupId, firstDate, secondDate);
    }

}
