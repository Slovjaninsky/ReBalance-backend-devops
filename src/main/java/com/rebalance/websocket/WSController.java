package com.rebalance.websocket;

import com.rebalance.dto.request.SeenNotificationsRequest;
import com.rebalance.entity.User;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WSController {
    private final NotificationService notificationService;

    @MessageMapping("/notifications-seen")
    public void setNotificationsAsSeen(SeenNotificationsRequest request, Principal principal) {
        if (principal == null) {
            throw new RebalanceException(RebalanceErrorType.RB_401);
        }
        User signedInUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        notificationService.setSeenByUser(signedInUser.getId(), request.getIds());
    }
}
