package com.rebalance.repository;

import com.rebalance.entity.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long> {
    List<NotificationUser> findAllByUserIdAndNotificationIdIn(Long userId, List<Long> notificationIds);
}
