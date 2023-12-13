package com.rebalance.service;

import com.rebalance.dto.response.NotificationAllResponse;
import com.rebalance.dto.response.NotificationResponse;
import com.rebalance.entity.*;
import com.rebalance.repository.NotificationRepository;
import com.rebalance.repository.NotificationUserRepository;
import com.rebalance.security.SignedInUsernameGetter;
import com.rebalance.websocket.WSService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationUserRepository notificationUserRepository;
    private final WSService wsService;
    private final SignedInUsernameGetter signedInUsernameGetter;

    public void saveNotificationUserAddedToGroup(User initiator, User added, Group group) {
        Notification notificationForUser = saveNotification(initiator, added, null, group, NotificationType.CurrentUserAddedToGroup);
        // send notification to user which was added to group in private channel
        saveAndSendToUser(notificationForUser, initiator, null, null, group, added, NotificationType.CurrentUserAddedToGroup, false);
        // send notification to user which was added to group in all channel
        saveAndSendToUser(notificationForUser, initiator, null, null, group, added, NotificationType.CurrentUserAddedToGroup, true);

        // send notification to all users of group that new user was added to group
        Notification notificationForOther = saveNotification(initiator, added, null, group, NotificationType.UserAddedToGroup);
        List<User> usersToSend = group.getUsers().stream().map(UserGroup::getUser)
                .filter(u -> u.getId() != added.getId()).toList();
        // send notifications to all users in group in private channel
        saveAndSendToUsers(notificationForOther, initiator, added, null, group,
                usersToSend, NotificationType.UserAddedToGroup, false);
        // send notifications to all users in group in all channel
        saveAndSendToUsers(notificationForOther, initiator, added, null, group,
                usersToSend, NotificationType.UserAddedToGroup, true);
    }

    public void saveNotificationCreatedGroup(User initiator, Group group) {
        Notification notification = saveNotification(initiator, null, null, group, NotificationType.GroupCreated);
        // send notification to all users in group in all channel
        saveAndSendToUsers(notification, initiator, null, null, group, List.of(initiator), NotificationType.GroupCreated, true);
    }

    public void saveNotificationGroupExpense(User initiator, Expense expense, Group group, List<ExpenseUsers> participants, NotificationType actionType) {
        Notification notification = saveNotification(initiator, null, expense, group, actionType);
        // send notification to all participants of expense in private channel
        saveAndSendToUsers(notification, initiator, null, expense, group, participants.stream().map(ExpenseUsers::getUser).toList(), actionType, false);
        // send notification to all users in group in all channel
        saveAndSendToUsers(notification, initiator, null, expense, group, group.getUsers().stream().map(UserGroup::getUser).toList(), actionType, true);
    }

    public void saveNotificationPersonalExpense(User initiator, Expense expense, NotificationType actionType) {
        Notification notification = saveNotification(initiator, null, expense, null, actionType);
        // send notification to user about personal expense in private channel
        saveAndSendToUser(notification, initiator, null, expense, null, initiator, actionType, false);
        // send notification to user about personal expense in all channel
        saveAndSendToUser(notification, initiator, null, expense, null, initiator, actionType, true);
    }

    public List<NotificationResponse> findAllNotSeen() {
        User signedInUser = signedInUsernameGetter.getUser();
        List<Notification> notifications = notificationRepository.findAllByNotificationUsersUserIdAndNotificationUsersSeen(signedInUser.getId(), false);

        return notifications.stream().map(notification -> NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .initiatorUserId(notification.getInitiator().getId())
                .userAddedId(notification.getAdded() == null ? null : notification.getAdded().getId())
                .expenseId(notification.getExpenseId())
                .groupId(notification.getGroup() == null ? null : notification.getGroup().getId())
                .message(notification.getMessage())
                .build()).toList();
    }

    //TODO: return to Page
    public List<NotificationAllResponse> findAllAfterDate(Date date) {
        User signedInUser = signedInUsernameGetter.getUser();
        LocalDateTime dateLocal = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
        List<Notification> notifications = notificationRepository.findAllByNotificationUsersUserIdAndDateAfter(
                signedInUser.getId(), dateLocal, Sort.by(Sort.Direction.ASC, "date"));

        return notifications.stream().map(notification -> NotificationAllResponse.builder()
                .type(notification.getType().ordinal())
                .initiatorUserId(notification.getInitiator().getId())
                .userAddedId(notification.getAdded() == null ? null : notification.getAdded().getId())
                .expenseId(notification.getExpenseId())
                .groupId(notification.getGroup() == null ? null : notification.getGroup().getId())
                .date(notification.getDate())
                .build()).toList();
    }

    private Notification saveNotification(User initiator, User added, Expense expense, Group group, NotificationType actionType) {
        return notificationRepository.save(Notification.builder()
                .type(actionType)
                .initiator(initiator)
                .added(added)
                .expenseId(expense == null ? null : expense.getId())
                .expenseDescription(expense == null ? null : expense.getDescription())
                .group(group)
                .date(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.of("UTC")))
                .build());
    }

    private void saveAndSendToUsers(Notification notification, User initiator, User added, Expense expense, Group group,
                                    List<User> participants, NotificationType actionType, Boolean sendToAll) {
        for (User participant : participants) {
            if (!sendToAll && participant.getId() == initiator.getId()) {
                continue;
            }
            saveAndSendToUser(notification, initiator, added, expense, group, participant, actionType, sendToAll);
        }
    }

    private void saveAndSendToUser(Notification notification, User initiator, User added, Expense expense, Group group,
                                   User receiver, NotificationType actionType, Boolean sendToAll) {
        notificationUserRepository.save(NotificationUser.builder()
                .seen(false)
                .user(receiver)
                .notification(notification)
                .build());

        if (sendToAll) {
            wsService.sendNotificationToUserAll(receiver.getEmail(), NotificationAllResponse.builder()
                    .type(actionType.ordinal())
                    .initiatorUserId(initiator.getId())
                    .userAddedId(added == null ? null : added.getId())
                    .expenseId(expense == null ? null : expense.getId())
                    .groupId(group == null ? null : group.getId())
                    .build());
        } else {
            wsService.sendNotificationToUser(receiver.getEmail(), NotificationResponse.builder()
                    .id(notification.getId())
                    .type(actionType)
                    .initiatorUserId(initiator.getId())
                    .userAddedId(added == null ? null : added.getId())
                    .expenseId(expense == null ? null : expense.getId())
                    .groupId(group == null ? null : group.getId())
                    .message(notification.getMessage())
                    .build());
        }
    }

    public void setSeenByUser(Long userId, List<Long> notificationIds) {
        notificationUserRepository.setUserNotificationsAsSeen(userId, notificationIds);
    }
}
