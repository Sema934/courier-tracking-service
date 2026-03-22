package com.migros.courier_tracking_service.repository;

import com.migros.courier_tracking_service.domain.Courier;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Courier c WHERE c.id = :id")
    Optional<Courier> findByIdWithLock(Long id);
}
