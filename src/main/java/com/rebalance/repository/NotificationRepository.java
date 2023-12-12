package com.rebalance.repository;

import com.rebalance.entity.Notification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByNotificationUsersUserIdAndNotificationUsersSeen(Long id, Boolean seen);

    List<Notification> findAllByNotificationUsersUserIdAndDateAfter(Long id, LocalDateTime dateLocal, Sort sort);
}
