package com.migros.courier_tracking_service.controller;

import com.migros.courier_tracking_service.request.LocationUpdateRequest;
import com.migros.courier_tracking_service.service.CourierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @PostMapping("/location")
    @ResponseStatus(HttpStatus.OK)
    public void updateLocation(@Valid @RequestBody LocationUpdateRequest request) {
        courierService.updateLocation(request.courier(), request.lat(), request.lng(), request.time());
    }

    @GetMapping("/{courierId}/total-distance")
    @ResponseStatus(HttpStatus.OK)
    public Double getTotalDistance(@PathVariable Long courierId) {
        return courierService.getTotalTravelDistance(courierId);
    }
}
