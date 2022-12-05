package com.example.databaseservice.repositories;

import com.example.databaseservice.entities.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<ExpenseGroup, Long> {
}
