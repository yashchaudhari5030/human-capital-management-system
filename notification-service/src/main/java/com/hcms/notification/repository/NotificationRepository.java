package com.hcms.notification.repository;

import com.hcms.notification.entity.Notification;
import com.hcms.notification.entity.NotificationChannel;
import com.hcms.notification.entity.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);
    Page<Notification> findByRecipientIdAndChannel(Long recipientId, NotificationChannel channel, Pageable pageable);
    List<Notification> findByStatus(NotificationStatus status);
    Page<Notification> findByRecipientIdAndStatus(Long recipientId, NotificationStatus status, Pageable pageable);
}

