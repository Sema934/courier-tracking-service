package com.migros.courier_tracking_service.exception;

import java.time.LocalDateTime;

public record ErrorResponse (
    LocalDateTime timestamp,
    String message,
    int status
) {
}
