package com.rebalance.repository;

import com.rebalance.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    Long countByGroupIdAndUserId(Long groupId, Long userId);

    Long countByGroupIdAndUserIdIn(Long groupId, Set<Long> users);
}