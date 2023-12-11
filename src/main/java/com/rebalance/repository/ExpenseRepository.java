package com.rebalance.repository;

import com.rebalance.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    void deleteById(Long expenseId);

    Page<Expense> findAllByGroupId(Long id, Pageable pageable);

    List<Expense> findAllByGroupIdAndIdIn(Long id, List<Long> expenseIds, Sort sort);
}
