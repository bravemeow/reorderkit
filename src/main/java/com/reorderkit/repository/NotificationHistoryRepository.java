package com.reorderkit.repository;

import com.reorderkit.entity.NotificationHistory;
import com.reorderkit.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    boolean existsByStoreAndNotificationDateAndSuccessfulTrue(
            Store store,
            LocalDate notificationDate
    );

    Optional<NotificationHistory> findTopByStoreAndNotificationDateOrderByAttemptNumberDesc(
            Store store,
            LocalDate notificationDate
    );
}
