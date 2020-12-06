package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Trip {

    private String departureLocation;
    private String arrivalLocation;
    private Float deliveryCost;
    private Float distance;
    private String category;

    public Trip(String departureLocation, String arrivalLocation, Float deliveryCost,
                Float distance, String category) {
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.deliveryCost = deliveryCost;
        this.distance = distance;
        this.category = category;
    }
}
