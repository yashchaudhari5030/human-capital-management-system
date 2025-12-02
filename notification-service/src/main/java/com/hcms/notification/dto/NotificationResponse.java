package com.hcms.notification.dto;

import com.hcms.notification.entity.NotificationChannel;
import com.hcms.notification.entity.NotificationStatus;
import com.hcms.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private Long recipientId;
    private NotificationType notificationType;
    private NotificationChannel channel;
    private String subject;
    private String message;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

