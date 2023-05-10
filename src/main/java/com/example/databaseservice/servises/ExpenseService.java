package com.example.databaseservice.servises;

import com.example.databaseservice.entities.Expense;
import com.example.databaseservice.exceptions.ExpenseNotFoundException;
import com.example.databaseservice.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void throwExceptionIfExpensesWithGlobalIdNotFound(Long globalId){
        if(expenseRepository.findByGlobalId(globalId).isEmpty()){
            throw new ExpenseNotFoundException(String.format("Expenses with globalId = %d not found", globalId));
        }
    }

    public List<Expense> getExpensesByGroupIdAndBetweenDates(Long groupId, LocalDate firstDate, LocalDate secondDate) {
        return expenseRepository.findByGroupIdAndDateStampBetween(groupId, firstDate, secondDate);
    }

    public Set<String> getAllExpenseDatesFromGroup(Long groupId) {
        return expenseRepository.findAllDateStampsByGroup(groupId)
                .stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .collect(Collectors.toSet());
    }

    public Long getMaxGlobalId(){
        return expenseRepository.getMaxGlobalId();
    }

}
