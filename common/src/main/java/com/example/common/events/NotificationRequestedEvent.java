package com.example.common.events;


import com.example.common.enums.EventType;
import com.example.common.enums.NotificationType;

public class NotificationRequestedEvent extends Event {
    private String userId;
    private String orderId;
    private NotificationType notificationType;

    public NotificationRequestedEvent(String userId, String orderId, NotificationType notificationType) {
        this.userId = userId;
        this.orderId = orderId;
        this.notificationType = notificationType;
//        type = "NOTIFICATION_REQUESTED";
        type = EventType.NOTIFICATION_REQUESTED;
    }

    public NotificationRequestedEvent() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
}
