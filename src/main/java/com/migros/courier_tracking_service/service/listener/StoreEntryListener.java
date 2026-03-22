package com.migros.courier_tracking_service.service.listener;

import com.migros.courier_tracking_service.domain.Store;
import com.migros.courier_tracking_service.domain.StoreEntryLog;
import com.migros.courier_tracking_service.repository.StoreEntryLogRepository;
import com.migros.courier_tracking_service.repository.StoreRepository;
import com.migros.courier_tracking_service.service.event.CourierLocationUpdatedEvent;
import com.migros.courier_tracking_service.service.strategy.DistanceCalculatorStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoreEntryListener {

    private final StoreRepository storeRepository;
    private final StoreEntryLogRepository storeEntryLogRepository;
    private final DistanceCalculatorStrategy distanceCalculatorStrategy;

    private static final double ENTRY_RADIUS_METERS = 100.0;

    @Async
    @EventListener
    public void handleLocationUpdate(CourierLocationUpdatedEvent event) {
        log.info("Processing location event for courier: {}", event.getCourierId());

        List<Store> stores = storeRepository.findAll();

        for (Store store : stores) {
            double distance = distanceCalculatorStrategy.calculateDistance(
                    event.getLat(), event.getLng(), store.getLat(), store.getLng());

            if (distance <= ENTRY_RADIUS_METERS) {
                checkAndLogEntry(event, store);
            }
        }
    }

    private void checkAndLogEntry(CourierLocationUpdatedEvent event, Store store) {
        LocalDateTime oneMinuteAgo = event.getLocalDateTime().minusMinutes(1);

        boolean hasEnteredRecently = storeEntryLogRepository
                .existsByCourierIdAndStoreNameAndEntryTimeAfter(event.getCourierId(), store.getName(), oneMinuteAgo);

        if (!hasEnteredRecently) {
            StoreEntryLog entryLog = StoreEntryLog.builder()
                    .courierId(event.getCourierId())
                    .storeName(store.getName())
                    .entryTime(event.getLocalDateTime())
                    .build();

            storeEntryLogRepository.save(entryLog);
            log.info("Logged entrance. Courier {} entered 100m radius of store {} at {}",
                    event.getCourierId(), store.getName(), event.getLocalDateTime());
        } else {
            log.info("Courier {} re-entered store {} within 1 minute. Entrance is ignored.",
                    event.getCourierId(), store.getName());
        }
    }
}
