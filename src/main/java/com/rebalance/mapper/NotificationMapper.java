package com.rebalance.mapper;

import com.rebalance.dto.response.NotificationResponse;
import com.rebalance.entity.Notification;
import org.mapstruct.Mapper;

@Mapper
public interface NotificationMapper {
    NotificationResponse notificationToResponse(Notification notification);
}
