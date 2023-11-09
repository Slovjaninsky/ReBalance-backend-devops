package com.rebalance.repository;

import com.rebalance.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByGroupsGroupId(Long id, Sort sort);

    Optional<Category> findByNameLike(String name);
}
