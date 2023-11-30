package com.rebalance.repository;

import com.rebalance.dto.response.SumByCategoryResponse;
import com.rebalance.entity.Group;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByCreatorIdAndPersonal(Long userId, Boolean personal);

    Boolean existsByIdAndCreatorIdAndPersonal(Long groupId, Long userId, Boolean b);

    Boolean existsByIdAndPersonal(Long groupId, Boolean b);

    List<Group> findAllByUsersUserIdAndPersonal(Long id, Boolean b);

    @Query("SELECT e.category.category.name, sum(e.amount) " +
            "FROM Expense e " +
            "WHERE e.group.id = :groupId " +
            "GROUP BY e.category.category.name")
    List<Tuple> findAllSumsByCategory(Long groupId);
}
