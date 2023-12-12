package com.rebalance.websocket;

import com.rebalance.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WSService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(String email, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(email, "/notifications/new", List.of(notification));
    }

    public void sendNotificationToUserAll(String email, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(email, "/notifications/new/all", List.of(notification));
    }
}
