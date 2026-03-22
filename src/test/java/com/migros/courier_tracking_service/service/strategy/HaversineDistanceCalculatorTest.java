package com.migros.courier_tracking_service.service.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HaversineDistanceCalculatorTest {

    private HaversineDistanceCalculator haversineDistanceCalculator;

    @BeforeEach
    void setUp() {
        haversineDistanceCalculator = new HaversineDistanceCalculator();
    }

    @Test
    void it_should_calculate_distance() {
        //Given
        double lat1 = 41.0082;
        double lng1 = 28.9784;
        double lat2 = 39.9334;
        double lng2 = 32.8597;

        //When
        double distance = haversineDistanceCalculator.calculateDistance(lat1, lng1, lat2, lng2);

        //Than
        assertEquals(349355.73928625206, distance);
    }

    @Test
    void it_should_calculate_distance_and_return_zero_for_same_point() {
        //Given
        double lat = 41.0082;
        double lng = 28.9784;

        //When
        double distance = haversineDistanceCalculator.calculateDistance(lat, lng, lat, lng);

        assertEquals(0, distance);
    }
}