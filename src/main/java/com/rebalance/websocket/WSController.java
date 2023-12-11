package com.rebalance.websocket;

import com.rebalance.dto.request.SeenNotificationsRequest;
import com.rebalance.dto.response.NotificationResponse;
import com.rebalance.entity.User;
import com.rebalance.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WSController {
    private final NotificationService notificationService;

    @MessageMapping("/notifications")
    @SendToUser("/notifications/new")
    public List<NotificationResponse> getMyNotifications(Principal principal) {
        User signedInUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return notificationService
                .findAllNotSeenByUserId(signedInUser.getId());
    }

    @MessageMapping("/notifications-seen")
    public void setNotificationsAsSeen(SeenNotificationsRequest request, Principal principal) {
        User signedInUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        notificationService.setSeenByUser(signedInUser.getId(), request.getIds());
    }
}
