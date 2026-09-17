package com.reorderkit;

import com.reorderkit.entity.NotificationHistory;
import com.reorderkit.entity.Store;
import com.reorderkit.repository.NotificationHistoryRepository;
import com.reorderkit.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NotificationHistoryRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;

    @Test
    void successfulNotificationExistsOnlyAfterSuccessfulAttempt() {
        Store store = saveStore("success");
        LocalDate notificationDate = LocalDate.of(2026, 9, 15);

        notificationHistoryRepository.saveAndFlush(NotificationHistory.failure(
                store,
                notificationDate,
                "merchant@example.com",
                1,
                "Email provider unavailable"
        ));

        boolean existsAfterFailure =
                notificationHistoryRepository.existsByStoreAndNotificationDateAndSuccessfulTrue(
                        store,
                        notificationDate
                );

        notificationHistoryRepository.saveAndFlush(NotificationHistory.success(
                store,
                notificationDate,
                "merchant@example.com",
                2
        ));

        boolean existsAfterSuccess =
                notificationHistoryRepository.existsByStoreAndNotificationDateAndSuccessfulTrue(
                        store,
                        notificationDate
                );

        assertThat(existsAfterFailure).isFalse();
        assertThat(existsAfterSuccess).isTrue();
    }

    @Test
    void findLatestAttemptForStoreAndDate() {
        Store store = saveStore("retry");
        LocalDate notificationDate = LocalDate.of(2026, 9, 15);

        notificationHistoryRepository.saveAndFlush(NotificationHistory.failure(
                store,
                notificationDate,
                "merchant@example.com",
                1,
                "First failure"
        ));
        notificationHistoryRepository.saveAndFlush(NotificationHistory.failure(
                store,
                notificationDate,
                "merchant@example.com",
                2,
                "Second failure"
        ));
        NotificationHistory thirdAttempt = notificationHistoryRepository.saveAndFlush(
                NotificationHistory.failure(
                        store,
                        notificationDate,
                        "merchant@example.com",
                        3,
                        "Final failure"
                )
        );

        Optional<NotificationHistory> latestAttempt =
                notificationHistoryRepository.findTopByStoreAndNotificationDateOrderByAttemptNumberDesc(
                        store,
                        notificationDate
                );

        assertThat(latestAttempt).isPresent();

        NotificationHistory retrievedAttempt = latestAttempt.orElseThrow();
        assertThat(retrievedAttempt.getId()).isEqualTo(thirdAttempt.getId());
        assertThat(retrievedAttempt.getAttemptNumber()).isEqualTo(3);
        assertThat(retrievedAttempt.isSuccessful()).isFalse();
        assertThat(retrievedAttempt.getFailureMessage()).isEqualTo("Final failure");
    }

    private Store saveStore(String suffix) {
        return storeRepository.saveAndFlush(new Store(
                "Shop Id " + suffix,
                suffix + ".myshopify.com",
                "Store " + suffix,
                "Access Token " + suffix,
                suffix + "@example.com"
        ));
    }
}
