package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewTruck {

    private Integer id;
    private String model;
    private Integer maxLoad;
    private Float fuelConsumption;
    private Integer price;
}
