package com.hcms.leave.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private Long recipientId;
    private String notificationType;
    private String channel;
    private String subject;
    private String message;
}

