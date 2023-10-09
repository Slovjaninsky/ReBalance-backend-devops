package com.rebalance.repositories;

import com.rebalance.entities.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<ExpenseGroup, Long> {
}
