package com.hcms.notification.service;

import com.hcms.notification.dto.NotificationRequest;
import com.hcms.notification.dto.NotificationResponse;
import com.hcms.notification.entity.Notification;
import com.hcms.notification.entity.NotificationStatus;
import com.hcms.notification.exception.ResourceNotFoundException;
import com.hcms.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        log.info("Creating notification for recipient: {}, type: {}, channel: {}", 
                request.getRecipientId(), request.getNotificationType(), request.getChannel());

        Notification notification = Notification.builder()
                .recipientId(request.getRecipientId())
                .notificationType(request.getNotificationType())
                .channel(request.getChannel())
                .subject(request.getSubject())
                .message(request.getMessage())
                .status(NotificationStatus.PENDING)
                .build();

        notification = notificationRepository.save(notification);

        // Send notification asynchronously (in production, use RabbitMQ)
        try {
            sendNotification(notification);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(java.time.LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
        }

        notification = notificationRepository.save(notification);
        log.info("Notification created: {}", notification.getId());

        return mapToResponse(notification);
    }

    private void sendNotification(Notification notification) {
        switch (notification.getChannel()) {
            case EMAIL:
                emailService.sendEmail(notification);
                break;
            case SMS:
                smsService.sendSms(notification);
                break;
            case IN_APP:
                // In-app notifications are stored in DB and retrieved via API
                log.info("In-app notification stored for recipient: {}", notification.getRecipientId());
                break;
            default:
                throw new IllegalArgumentException("Unsupported notification channel: " + notification.getChannel());
        }
    }

    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return mapToResponse(notification);
    }

    public Page<NotificationResponse> getNotificationsByRecipient(Long recipientId, Pageable pageable) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable)
                .map(this::mapToResponse);
    }

    public Page<NotificationResponse> getAllNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        notification.setStatus(NotificationStatus.READ);
        notification = notificationRepository.save(notification);
        return mapToResponse(notification);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipientId())
                .notificationType(notification.getNotificationType())
                .channel(notification.getChannel())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .errorMessage(notification.getErrorMessage())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}

