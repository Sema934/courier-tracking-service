package com.migros.courier_tracking_service.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record LocationUpdateRequest(

    @NotNull(message = "{location.time.required}")
    LocalDateTime time,

    @NotNull(message = "{location.courier.required}")
    Long courier,

    @NotNull(message = "{location.lat.required}")
    Double lat,

    @NotNull(message = "{location.lng.required}")
    Double lng
) {
}
