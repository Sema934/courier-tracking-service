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
@Table(name = "store_entry_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEntryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "courier_id", nullable = false)
    private Long courierId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;
}
