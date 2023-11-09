package com.rebalance.repository;

import com.rebalance.entity.Category;
import com.rebalance.entity.Group;
import com.rebalance.entity.GroupCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupCategoryRepository extends JpaRepository<GroupCategory, Long> {
    Optional<GroupCategory> findByCategoryAndGroup(Category category, Group group);
}
