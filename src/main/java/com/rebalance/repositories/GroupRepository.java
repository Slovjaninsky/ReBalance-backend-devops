package com.rebalance.repositories;

import com.rebalance.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByCreatorIdAndPersonal(Long userId, Boolean personal);

    Boolean existsByIdAndCreatorIdAndPersonal(Long groupId, Long userId, Boolean b);
}
