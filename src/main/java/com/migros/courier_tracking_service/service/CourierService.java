package com.migros.courier_tracking_service.service;

import java.time.LocalDateTime;

public interface CourierService {

    void updateLocation(Long courierId, Double lat, Double lng, LocalDateTime timestamp);

    Double getTotalTravelDistance(Long courierId);
}
