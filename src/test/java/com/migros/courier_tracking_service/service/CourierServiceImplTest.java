package com.migros.courier_tracking_service.service;

import com.migros.courier_tracking_service.domain.Courier;
import com.migros.courier_tracking_service.exception.CourierTrackingException;
import com.migros.courier_tracking_service.repository.CourierRepository;
import com.migros.courier_tracking_service.service.event.CourierLocationUpdatedEvent;
import com.migros.courier_tracking_service.service.strategy.DistanceCalculatorStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourierServiceImplTest {

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private DistanceCalculatorStrategy distanceCalculator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CourierServiceImpl courierService;

    @Test
    void should_throw_exception_when_courier_not_found_for_update_location() {
        //Given
        Long courierId = 2L;
        Double lat = 32.0;
        Double lng = 25.0;
        LocalDateTime time = LocalDateTime.now();
        when(courierRepository.findByIdWithLock(courierId)).thenReturn(Optional.empty());

        //When & Then
        CourierTrackingException exception = assertThrows(CourierTrackingException.class,
                () -> courierService.updateLocation(courierId, lat, lng, time));

        assertEquals("Courier not found with id: " + courierId, exception.getMessage());
        verify(courierRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

   /* @Test
    void it_should_update_location_when_courier_does_not_exists() {
        //Given
        Long courierId = 2L;
        Double lat = 32.0;
        Double lng = 25.0;
        LocalDateTime time = LocalDateTime.now();

        when(courierRepository.findByIdWithLock(courierId)).thenReturn(Optional.empty());

        //When
        courierService.updateLocation(courierId, lat, lng, time);

        //Then
        verify(courierRepository).findByIdWithLock(courierId);

        ArgumentCaptor<Courier> courierArgumentCaptor = ArgumentCaptor.forClass(Courier.class);
        verify(courierRepository).save(courierArgumentCaptor.capture());

        Courier savedCourier = courierArgumentCaptor.getValue();
        assertEquals(courierId, savedCourier.getId());
        assertEquals(lat, savedCourier.getLastLat());
        assertEquals(lng, savedCourier.getLastLng());
        assertEquals(time, savedCourier.getLastUpdateTime());
        assertEquals(0.0, savedCourier.getTotalDistance());

        ArgumentCaptor<CourierLocationUpdatedEvent> eventCaptor =
                ArgumentCaptor.forClass(CourierLocationUpdatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        CourierLocationUpdatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(courierId, publishedEvent.getCourierId());
        assertEquals(lat, publishedEvent.getLat());
        assertEquals(lng, publishedEvent.getLng());
        assertEquals(time, publishedEvent.getLocalDateTime());
    } */

    @Test
    void it_should_update_location_when_courier_exists() {
        //Given
        Long courierId = 2L;
        Double lat = 40.0;
        Double lng = 29.0;
        LocalDateTime time = LocalDateTime.now();

        Courier courier = Courier.builder()
                .id(courierId)
                .lastLat(41.0)
                .lastLng(28.0)
                .totalDistance(150.0)
                .build();

        when(courierRepository.findByIdWithLock(courierId)).thenReturn(Optional.of(courier));
        when(distanceCalculator.calculateDistance(41.0, 28.0, lat, lng)).thenReturn(50.0);

        //When
        courierService.updateLocation(courierId, lat, lng, time);

        //Then
        verify(courierRepository).findByIdWithLock(courierId);
        verify(distanceCalculator).calculateDistance(41.0, 28.0, lat, lng);

        ArgumentCaptor<Courier> courierArgumentCaptor = ArgumentCaptor.forClass(Courier.class);
        verify(courierRepository).save(courierArgumentCaptor.capture());

        Courier savedCourier = courierArgumentCaptor.getValue();
        assertEquals(courierId, savedCourier.getId());
        assertEquals(lat, savedCourier.getLastLat());
        assertEquals(lng, savedCourier.getLastLng());
        assertEquals(time, savedCourier.getLastUpdateTime());
        assertEquals(200.0, savedCourier.getTotalDistance());

        ArgumentCaptor<CourierLocationUpdatedEvent> eventCaptor =
                ArgumentCaptor.forClass(CourierLocationUpdatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        CourierLocationUpdatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(courierId, publishedEvent.getCourierId());
        assertEquals(lat, publishedEvent.getLat());
        assertEquals(lng, publishedEvent.getLng());
        assertEquals(time, publishedEvent.getLocalDateTime());
    }

    @Test
    void it_should_get_total_travel_distance() {
        //Given
        Long courierId = 4L;
        Double totalDistance = 257.0;
        Courier courier = Courier.builder().id(courierId).totalDistance(totalDistance).build();

        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        //When
        Double result = courierService.getTotalTravelDistance(courierId);

        //Then
        assertEquals(totalDistance, result);
    }

    @Test
    void it_should_throw_exception_when_courier_not_found_for_total_travel_distance() {
        //Given
        Long courierId = 4L;
        when(courierRepository.findById(courierId)).thenReturn(Optional.empty());

        //When & Then
        CourierTrackingException exception = assertThrows(CourierTrackingException.class,
                () -> courierService.getTotalTravelDistance(courierId));

        assertEquals("Courier not found with id: " + courierId, exception.getMessage());
    }
}