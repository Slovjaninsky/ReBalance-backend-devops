package com.rebalance.repositories;

import com.rebalance.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    void deleteById(Long expenseId);

    List<Expense> findByGroupIdAndDateBetween(Long groupId, LocalDate firstDate, LocalDate secondDate);

    @Query("SELECT e.date FROM Expense e where e.group.id = :groupId")
    List<LocalDate> findAllDateStampsByGroup(@Param("groupId") Long groupId);

    List<Expense> findAllByGroupId(Long id);
}
