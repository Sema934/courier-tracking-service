package com.migros.courier_tracking_service.repository;

import com.migros.courier_tracking_service.domain.StoreEntryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface StoreEntryLogRepository extends JpaRepository<StoreEntryLog, Long> {

    boolean existsByCourierIdAndStoreNameAndEntryTimeAfter(Long courierId, String storeName, LocalDateTime time);

}
