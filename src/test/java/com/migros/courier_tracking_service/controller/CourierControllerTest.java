package com.migros.courier_tracking_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migros.courier_tracking_service.request.LocationUpdateRequest;
import com.migros.courier_tracking_service.service.CourierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourierController.class)
class CourierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourierService courierService;

    @Test
    void it_should_update_location() throws Exception {
        //Given
        LocalDateTime time = LocalDateTime.now();
        LocationUpdateRequest locationUpdateRequest =
                new LocationUpdateRequest(time, 4L, 41.0082, 28.9784);

        //When
        mockMvc.perform(post("/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationUpdateRequest)))
                .andExpect(status().isOk());

        //Then
        verify(courierService).updateLocation(eq(4L), eq(41.0082), eq(28.9784), eq(time));
    }

    @Test
    void it_should_return_bad_request_when_lat_is_missing_for_update_location() throws Exception {
        //Given
        LocalDateTime time = LocalDateTime.now();
        LocationUpdateRequest locationUpdateRequest =
                new LocationUpdateRequest(time, 4L, null, 28.9784);

        //When && Then
        mockMvc.perform(post("/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.lat").value("Latitude is required"));
    }

    @Test
    void it_should_return_bad_request_when_all_fields_are_missing_for_update_location() throws Exception {
        //Given
        Map<String, Object> emptyMap = Collections.emptyMap();

        //When && Then
        mockMvc.perform(post("/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyMap)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.courier").value("Courier ID is required"))
                .andExpect(jsonPath("$.lat").value("Latitude is required"))
                .andExpect(jsonPath("$.lng").value("Longitude is required"))
                .andExpect(jsonPath("$.time").value("Time is required"));
    }

    @Test
    void it_should_get_total_distance() throws Exception {
        //Given
        Long courierId = 4L;
        Double expectedDistance = 350.5;

        //When
        when(courierService.getTotalTravelDistance(courierId)).thenReturn(expectedDistance);
        mockMvc.perform(get("/couriers/{courierId}/total-distance", courierId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedDistance)));

        //Then
        verify(courierService).getTotalTravelDistance(courierId);
    }
}