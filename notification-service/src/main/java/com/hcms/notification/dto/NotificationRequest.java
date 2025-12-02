package com.hcms.notification.dto;

import com.hcms.notification.entity.NotificationChannel;
import com.hcms.notification.entity.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;

    @NotNull(message = "Channel is required")
    private NotificationChannel channel;

    @NotNull(message = "Subject is required")
    private String subject;

    @NotNull(message = "Message is required")
    private String message;
}

