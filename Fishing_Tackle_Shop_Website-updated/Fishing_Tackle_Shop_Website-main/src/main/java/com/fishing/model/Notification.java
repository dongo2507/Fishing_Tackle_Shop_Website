package com.fishing.model;

import com.fishing.enums.NotificationType;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Thông báo gửi tới một khách hàng (lưu trong database). */
@Entity
@Table(name = "notifications", indexes = @Index(name = "idx_notification_recipient", columnList = "recipient_id"))
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    /** Admin gửi (null nếu hệ thống tự gửi). */
    @Column(name = "sender_id")
    private Long senderId;

    /** Id khách nhận. */
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    public Notification() {}

    public Notification(Long recipientId, NotificationType type, String title, String content) {
        this.recipientId = recipientId;
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** "Vừa xong", "5 phút trước", "2 giờ trước", hoặc ngày giờ nếu đã lâu. */
    public String getTimeText() {
        if (sentAt == null) {
            return "";
        }
        long minutes = Duration.between(sentAt, LocalDateTime.now()).toMinutes();
        if (minutes < 1) {
            return "Vừa xong";
        }
        if (minutes < 60) {
            return minutes + " phút trước";
        }
        if (minutes < 24 * 60) {
            return (minutes / 60) + " giờ trước";
        }
        return sentAt.format(DATE_FMT);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { this.isRead = read; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }
}