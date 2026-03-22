package com.migros.courier_tracking_service.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class HaversineDistanceCalculator implements DistanceCalculatorStrategy {

    private static final double EARTH_RADIUS_METERS = 6371000.0;

    @Override
    public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double startLatRad = Math.toRadians(lat1);
        double endLatRad = Math.toRadians(lat2);

        double a = haversine(dLat) + Math.cos(startLatRad) * Math.cos(endLatRad) * haversine(dLng);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    private double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}
