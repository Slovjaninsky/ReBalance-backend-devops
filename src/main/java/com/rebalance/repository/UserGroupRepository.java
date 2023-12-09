package com.rebalance.repository;

import com.rebalance.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    Long countByGroupIdAndUserId(Long groupId, Long userId);

    Long countByGroupIdAndUserIdIn(Long groupId, Set<Long> users);

    Optional<UserGroup> findByUserIdAndGroupId(Long id, Long groupId);

    List<UserGroup> findByUserIdAndGroupIdIn(Long id, List<Long> groupIds);

    List<UserGroup> findAllByGroupIdAndUserIdIn(Long groupId, Set<Long> keySet);

    List<UserGroup> findAllByGroupId(Long groupId);
}