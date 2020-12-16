package com.myprojects.truckmanager.truckManagerApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentInfoDTO {

    private String departureCity;
    private String arrivalCity;
    private Integer income;
    private Float estimatedDistance;//TODO add estimated time
}
