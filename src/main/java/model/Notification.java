
package model;

import enums.NotificationType;
import java.util.Date;

public class Notification {

    private String notificationId;
    private NotificationType type;
    private String title;
    private String content;
    private boolean isRead;
    private Date sentAt;
    private String senderId;
    private String recipientId;

    public Notification() {
    }

    public Notification(String notificationId,
                        NotificationType type,
                        String title, String content,
                        boolean isRead, Date sentAt,
                        String senderId, String recipientId) {
        this.notificationId = notificationId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.sentAt = sentAt;
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public boolean send() {
        return type != null
                && title != null && !title.isBlank()
                && content != null && !content.isBlank()
                && recipientId != null && !recipientId.isBlank();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
