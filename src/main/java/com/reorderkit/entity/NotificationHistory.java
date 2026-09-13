package com.reorderkit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "notification_history")
public class NotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "notification_date", nullable = false)
    private LocalDate notificationDate;

    @Column(name = "recipient_email", nullable = false, length = 320)
    private String recipientEmail;

    @Column(name = "attempt_number", nullable = false)
    private int attemptNumber;

    @Column(name = "successful", nullable = false)
    private boolean successful;

    @Column(name = "attempted_at", nullable = false, updatable = false)
    private Instant attemptedAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "failure_message", columnDefinition = "TEXT")
    private String failureMessage;

    protected NotificationHistory() {
    }

    private NotificationHistory(
            Store store,
            LocalDate notificationDate,
            String recipientEmail,
            int attemptNumber
    ) {
        this.store = store;
        this.notificationDate = notificationDate;
        this.recipientEmail = recipientEmail;
        this.attemptNumber = attemptNumber;
        this.attemptedAt = Instant.now();
    }

    public static NotificationHistory success(
            Store store,
            LocalDate notificationDate,
            String recipientEmail,
            int attemptNumber
    ) {
        NotificationHistory history = new NotificationHistory(
                store,
                notificationDate,
                recipientEmail,
                attemptNumber
        );
        history.successful = true;
        history.sentAt = Instant.now();
        return history;
    }

    public static NotificationHistory failure(
            Store store,
            LocalDate notificationDate,
            String recipientEmail,
            int attemptNumber,
            String failureMessage
    ) {
        NotificationHistory history = new NotificationHistory(
                store,
                notificationDate,
                recipientEmail,
                attemptNumber
        );
        history.successful = false;
        history.failureMessage = failureMessage;
        return history;
    }

    @PrePersist
    private void onCreate() {
        if (attemptedAt == null) {
            attemptedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Store getStore() {
        return store;
    }

    public LocalDate getNotificationDate() {
        return notificationDate;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Instant getAttemptedAt() {
        return attemptedAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public String getFailureMessage() {
        return failureMessage;
    }
}
