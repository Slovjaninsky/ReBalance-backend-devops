package com.rebalance.repositories;

import com.rebalance.entities.ExpenseUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseUsersRepository extends JpaRepository<ExpenseUsers, Long> {

}
