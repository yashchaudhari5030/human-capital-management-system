package com.hcms.leave.client;

import com.hcms.leave.client.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", path = "/api/notifications")
public interface NotificationServiceClient {
    
    @PostMapping
    void createNotification(@RequestBody NotificationRequest request);
}

