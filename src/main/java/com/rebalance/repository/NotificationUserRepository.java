package com.rebalance.repository;

import com.rebalance.entity.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long> {
    List<NotificationUser> findAllByUserIdAndNotificationIdIn(Long userId, List<Long> notificationIds);

    @Transactional
    @Modifying
    @Query("UPDATE NotificationUser nu SET nu.seen = TRUE WHERE nu.user.id = :userId AND nu.notification.id IN :notificationIds")
    void setUserNotificationsAsSeen(Long userId, List<Long> notificationIds);
}
