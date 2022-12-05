package com.example.databaseservice.repositories;

import com.example.databaseservice.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Expense e WHERE e.globalId=:globalId")
    void deleteByGlobalId(@Param("globalId") Long globalId);

    List<Expense> findByGlobalId(Long globalId);

}
