package com.example.databaseservice.expense;

import com.example.databaseservice.applicationuser.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void deleteByGlobalId(Long globalId){
        expenseRepository.deleteByGlobalId(globalId);
    }

    public List<Expense> getExpensesByGlobalId(Long globalId){
        return expenseRepository.findByGlobalId(globalId);
    }

}
