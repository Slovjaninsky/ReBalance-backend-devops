package com.rebalance.repository;

import com.rebalance.entity.ExpenseUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseUsersRepository extends JpaRepository<ExpenseUsers, Long> {

    void deleteAllByExpenseId(Long expenseId);
}
