package com.migros.courier_tracking_service.service;

import com.migros.courier_tracking_service.domain.Courier;
import com.migros.courier_tracking_service.exception.CourierTrackingException;
import com.migros.courier_tracking_service.repository.CourierRepository;
import com.migros.courier_tracking_service.service.event.CourierLocationUpdatedEvent;
import com.migros.courier_tracking_service.service.strategy.DistanceCalculatorStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierServiceImpl implements CourierService {

    private final CourierRepository courierRepository;
    private final DistanceCalculatorStrategy distanceCalculator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void updateLocation(Long courierId, Double lat, Double lng, LocalDateTime localDateTime) {
        log.info("Updating location for courier: {} at lat: {}, lng: {}", courierId, lat, lng);

        Courier courier = courierRepository.findByIdWithLock(courierId)
                .orElseThrow(() -> new CourierTrackingException("Courier not found with id: " + courierId));

        if (courier.getLastLat() != null && courier.getLastLng() != null) {
            double distance = distanceCalculator.calculateDistance(
                    courier.getLastLat(), courier.getLastLng(), lat, lng);
            courier.setTotalDistance(courier.getTotalDistance() + distance);
        }

        courier.setLastLat(lat);
        courier.setLastLng(lng);
        courier.setLastUpdateTime(localDateTime);

        courierRepository.save(courier);

        eventPublisher.publishEvent(CourierLocationUpdatedEvent.builder()
                .courierId(courierId)
                .lat(lat)
                .lng(lng)
                .localDateTime(localDateTime).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalTravelDistance(Long courierId) {
        return courierRepository.findById(courierId)
                .map(Courier::getTotalDistance)
                .orElseThrow(() -> new CourierTrackingException("Courier not found with id: " + courierId));
    }
}
