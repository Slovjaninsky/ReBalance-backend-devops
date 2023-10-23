package com.rebalance.repositories;

import com.rebalance.entities.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    Long countByGroupIdAndUserId(Long groupId, Long userId);

    Long countByGroupIdAndUserIdIn(Long groupId, List<Long> users);
}