package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Trip {

    private String departureLocation;
    private String arrivalLocation;
    private Float deliveryCost;
    private Float distance;
    private String category;
    private List<Truck> trucks;

    public Trip(String departureLocation, String arrivalLocation, Float deliveryCost,
                Float distance, String category) {
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.deliveryCost = deliveryCost;
        this.distance = distance;
        this.category = category;
    }

    public void addTruckToTrip(Truck truck) {
        if (trucks == null) {
            trucks = new ArrayList<>();
        }
        trucks.add(truck);
    }
}
