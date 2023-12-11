package com.rebalance.service;

import com.rebalance.dto.response.NotificationResponse;
import com.rebalance.entity.*;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.mapper.converter.DecimalConverter;
import com.rebalance.repository.NotificationRepository;
import com.rebalance.repository.NotificationUserRepository;
import com.rebalance.websocket.WSService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationUserRepository notificationUserRepository;
    private final WSService wsService;

    public void saveNotificationUserAddedToGroup(User initiator, User added, Group group) {
        Notification notification = new Notification();
        notification.setType(NotificationType.UserAddedToGroup);
        notification.setInitiator(initiator);
        notification.setGroup(group);
        notificationRepository.save(notification);

        NotificationUser notificationUser = new NotificationUser();
        notificationUser.setSeen(false);
        notificationUser.setUser(added);
        notificationUser.setNotification(notification);
        notificationUserRepository.save(notificationUser);

        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setType(NotificationType.UserAddedToGroup);
        response.setInitiatorUserId(initiator.getId());
        response.setGroupId(group.getId());
        response.setMessage(notification.getMessage());
        wsService.sendNotificationsToUser(added.getEmail(), List.of(response));
    }

    public void saveNotificationGroupExpense(User initiator, Expense expense, Group group, List<ExpenseUsers> participants, NotificationType acttionType) {
        Notification notification = new Notification();
        notification.setType(acttionType);
        notification.setInitiator(initiator);
        notification.setExpenseId(expense.getId());
        notification.setExpenseDescription(expense.getDescription());
        notification.setGroup(group);
        notificationRepository.save(notification);

        List<NotificationUser> notificationUsers = new ArrayList<>(participants.size());
        for (ExpenseUsers participant : participants) {
            NotificationUser notificationUser = new NotificationUser();
            notificationUser.setSeen(false);
            notificationUser.setUser(participant.getUser());
            notificationUser.setNotification(notification);
            notificationUsers.add(notificationUser);

            NotificationResponse response = new NotificationResponse();
            response.setId(notification.getId());
            response.setType(acttionType);
            response.setInitiatorUserId(initiator.getId());
            response.setExpenseId(expense.getId());
            response.setGroupId(group.getId());
            response.setMessage(notification.getMessage());
            wsService.sendNotificationsToUser(participant.getUser().getEmail(), List.of(response));
        }
        notificationUserRepository.saveAll(notificationUsers);
    }

    public List<NotificationResponse> findAllNotSeenByUserId(Long id) {
        List<Notification> notifications = notificationRepository.findAllByNotificationUsersUserIdAndNotificationUsersSeen(id, false);

        List<NotificationResponse> responses = new ArrayList<>(notifications.size());
        for (Notification notification : notifications) {
            NotificationResponse response = new NotificationResponse();
            response.setId(notification.getId());
            response.setType(notification.getType());
            response.setInitiatorUserId(notification.getInitiator().getId());
            response.setExpenseId(notification.getExpenseId());
            response.setGroupId(notification.getGroup().getId());
            response.setMessage(notification.getMessage());
            responses.add(response);
        }

        return responses;
    }

    public void setSeenByUser(Long userId, List<Long> notificationIds) {
        List<NotificationUser> notificationUsers = notificationUserRepository.findAllByUserIdAndNotificationIdIn(userId, notificationIds);

        if (notificationUsers.size() != notificationIds.size()) {
            throw new RebalanceException(RebalanceErrorType.RB_501);
        }

        notificationUsers.forEach(n -> n.setSeen(true));
        notificationUserRepository.saveAll(notificationUsers);
    }
}
