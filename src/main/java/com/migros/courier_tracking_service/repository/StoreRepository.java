package com.migros.courier_tracking_service.repository;

import com.migros.courier_tracking_service.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}

