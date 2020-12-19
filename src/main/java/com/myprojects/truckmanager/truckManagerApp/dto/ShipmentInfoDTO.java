package com.myprojects.truckmanager.truckManagerApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentInfoDTO {

    private Integer id;
    private String departureCity;
    private String arrivalCity;
    private Integer income;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Float estimatedDistance;
}
