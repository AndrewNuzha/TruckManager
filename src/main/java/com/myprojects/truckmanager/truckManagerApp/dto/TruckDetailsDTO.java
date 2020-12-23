package com.myprojects.truckmanager.truckManagerApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TruckDetailsDTO {

    private Long truckId;
    private Float mileage;
    private Float mileageBeforeService;
    private Float fuelConsumption;
    private String currentLocationText;
}
