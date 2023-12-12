package com.rebalance.controller;

import com.rebalance.dto.request.NotificationAllRequest;
import com.rebalance.dto.response.NotificationAllResponse;
import com.rebalance.dto.response.NotificationResponse;
import com.rebalance.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification management")
@AllArgsConstructor
@RestController
@RequestMapping(APIVersion.current + "/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Get new notifications for user")
    @GetMapping("/new")
    public List<NotificationResponse> getMyNotifications() {
        return notificationService.findAllNotSeen();
    }

    @Operation(summary = "Get all notifications for user after specified date")
    @PostMapping("/after-date")
    public List<NotificationAllResponse> getMyNotificationsAfter(@RequestBody @Validated NotificationAllRequest request) {
        return notificationService.findAllAfterDate(request.getDate());
    }
}
