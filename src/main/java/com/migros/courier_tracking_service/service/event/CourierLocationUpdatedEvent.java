package com.migros.courier_tracking_service.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CourierLocationUpdatedEvent {
    private final Long courierId;
    private final Double lat;
    private final Double lng;
    private final LocalDateTime localDateTime;
}
