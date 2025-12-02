package com.hcms.notification.service;

import com.hcms.notification.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {

    public void sendSms(Notification notification) {
        // In production, integrate with SMS service (Twilio, AWS SNS, etc.)
        log.info("Sending SMS to recipient: {}, message: {}", 
                notification.getRecipientId(), notification.getMessage());
        
        // Simulate SMS sending
        // In production: smsClient.send(recipientPhone, notification.getMessage());
    }
}



