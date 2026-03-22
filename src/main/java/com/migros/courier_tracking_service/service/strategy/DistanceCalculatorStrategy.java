package com.migros.courier_tracking_service.service.strategy;

public interface DistanceCalculatorStrategy {

    double calculateDistance(double lat1, double lng1, double lat2, double lng2);

}
