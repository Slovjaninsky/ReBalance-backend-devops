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

    public void sendNotificationsToUser(String email, List<NotificationResponse> notifications) {
        messagingTemplate.convertAndSendToUser(email, "/notifications/new", notifications);
    }
}
