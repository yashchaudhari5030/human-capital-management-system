package com.hcms.notification.service;

import com.hcms.notification.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    public void sendEmail(Notification notification) {
        // In production, integrate with email service (SendGrid, AWS SES, etc.)
        log.info("Sending email to recipient: {}, subject: {}", 
                notification.getRecipientId(), notification.getSubject());
        log.info("Email content: {}", notification.getMessage());
        
        // Simulate email sending
        // In production: emailClient.send(recipientEmail, notification.getSubject(), notification.getMessage());
    }
}



