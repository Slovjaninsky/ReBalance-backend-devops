package com.rebalance.controller;

import com.rebalance.dto.response.NotificationResponse;
import com.rebalance.mapper.NotificationMapper;
import com.rebalance.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Notification management")
@AllArgsConstructor
@RestController
@RequestMapping(APIVersion.current + "/user")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @Operation(summary = "Get new notifications for user")
    @GetMapping("/{id}/notifications")
    public ResponseEntity<List<NotificationResponse>> getAllNotificationsByUserId(@PathVariable(value = "id") Long userId) {
        return ResponseEntity.ok(
                notificationService.findAllByUserIdAndDeleteThem(userId).stream()
                        .map(notificationMapper::notificationToResponse).collect(Collectors.toList()));
    }
}
