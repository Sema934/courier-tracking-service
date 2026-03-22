package com.migros.courier_tracking_service.service.listener;

import com.migros.courier_tracking_service.domain.Store;
import com.migros.courier_tracking_service.domain.StoreEntryLog;
import com.migros.courier_tracking_service.repository.StoreEntryLogRepository;
import com.migros.courier_tracking_service.repository.StoreRepository;
import com.migros.courier_tracking_service.service.event.CourierLocationUpdatedEvent;
import com.migros.courier_tracking_service.service.strategy.DistanceCalculatorStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreEntryListenerTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreEntryLogRepository storeEntryLogRepository;

    @Mock
    private DistanceCalculatorStrategy distanceCalculatorStrategy;

    @InjectMocks
    private StoreEntryListener storeEntryListener;

    @Test
    void it_should_handle_location_update() {
        //Given
        Long courierId = 1L;
        Double lat = 41.0005;
        Double lng = 29.0005;
        LocalDateTime time = LocalDateTime.now();
        CourierLocationUpdatedEvent courierLocationUpdatedEvent = CourierLocationUpdatedEvent.builder()
                .courierId(courierId)
                .lat(lat)
                .lng(lng)
                .localDateTime(time).build();

        Store store = Store.builder().name("Ataşehir MMM Migros").lat(40.9923307).lng(29.1244229).build();

        when(storeRepository.findAll()).thenReturn(List.of(store));
        when(distanceCalculatorStrategy.calculateDistance(lat, lng, store.getLat(), store.getLng()))
                .thenReturn(85.0);
        when(storeEntryLogRepository.existsByCourierIdAndStoreNameAndEntryTimeAfter(
                eq(1L), eq(store.getName()), any(LocalDateTime.class))).thenReturn(false);

        //When
        storeEntryListener.handleLocationUpdate(courierLocationUpdatedEvent);

        //Then
        verify(storeRepository).findAll();
        verify(distanceCalculatorStrategy).calculateDistance(lat, lng, store.getLat(), store.getLng());
        verify(storeEntryLogRepository).existsByCourierIdAndStoreNameAndEntryTimeAfter(
                eq(1L), eq(store.getName()),any(LocalDateTime.class));

        ArgumentCaptor<StoreEntryLog> storeEntryLogArgumentCaptor = ArgumentCaptor.forClass(StoreEntryLog.class);
        verify(storeEntryLogRepository).save(storeEntryLogArgumentCaptor.capture());

        StoreEntryLog savedLog = storeEntryLogArgumentCaptor.getValue();
        assertEquals(1L, savedLog.getCourierId());
        assertEquals(store.getName(), savedLog.getStoreName());
        assertEquals(courierLocationUpdatedEvent.getLocalDateTime(), savedLog.getEntryTime());
    }

    @Test
    void it_should_not_log_entry_when_courier_beyond_100_meters() {
        //Given
        Long courierId = 1L;
        Double lat = 41.0005;
        Double lng = 27.0005;
        LocalDateTime time = LocalDateTime.now();
        CourierLocationUpdatedEvent courierLocationUpdatedEvent = CourierLocationUpdatedEvent.builder()
                .courierId(courierId)
                .lat(lat)
                .lng(lng)
                .localDateTime(time).build();

        Store store = Store.builder().name("Ataşehir MMM Migros").lat(40.9923307).lng(29.1244229).build();

        when(storeRepository.findAll()).thenReturn(List.of(store));
        when(distanceCalculatorStrategy.calculateDistance(lat, lng, store.getLat(), store.getLng()))
                .thenReturn(107.5);

        //When
        storeEntryListener.handleLocationUpdate(courierLocationUpdatedEvent);

        //Then
        verify(storeRepository).findAll();
        verify(distanceCalculatorStrategy).calculateDistance(lat, lng, store.getLat(), store.getLng());
        verify(storeEntryLogRepository, never()).existsByCourierIdAndStoreNameAndEntryTimeAfter(
                eq(1L), eq(store.getName()), any(LocalDateTime.class));
        verify(storeEntryLogRepository, never()).save(any(StoreEntryLog.class));
    }

    @Test
    void it_should_not_log_entry_when_courier_within_100_meters_but_entered_recently() {
        //Given
        Long courierId = 1L;
        Double lat = 41.0005;
        Double lng = 29.0005;
        LocalDateTime time = LocalDateTime.now();
        CourierLocationUpdatedEvent courierLocationUpdatedEvent = CourierLocationUpdatedEvent.builder()
                .courierId(courierId)
                .lat(lat)
                .lng(lng)
                .localDateTime(time).build();

        Store store = Store.builder().name("Ataşehir MMM Migros").lat(40.9923307).lng(29.1244229).build();

        when(storeRepository.findAll()).thenReturn(List.of(store));
        when(distanceCalculatorStrategy.calculateDistance(lat, lng, store.getLat(), store.getLng()))
                .thenReturn(85.0);
        when(storeEntryLogRepository.existsByCourierIdAndStoreNameAndEntryTimeAfter(
                eq(1L), eq(store.getName()), any(LocalDateTime.class))).thenReturn(true);

        //When
        storeEntryListener.handleLocationUpdate(courierLocationUpdatedEvent);

        //Then
        verify(storeRepository).findAll();
        verify(distanceCalculatorStrategy).calculateDistance(lat, lng, store.getLat(), store.getLng());
        verify(storeEntryLogRepository).existsByCourierIdAndStoreNameAndEntryTimeAfter(
                eq(1L), eq(store.getName()),any(LocalDateTime.class));
        verify(storeEntryLogRepository, never()).save(any(StoreEntryLog.class));
    }
}