package com.migros.courier_tracking_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "courier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Courier {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Builder.Default
    @Column(name = "total_distance", nullable = false)
    private Double totalDistance = 0.0;

    @Column(name = "last_lat")
    private Double lastLat;

    @Column(name = "last_lng")
    private Double lastLng;

    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;
}
