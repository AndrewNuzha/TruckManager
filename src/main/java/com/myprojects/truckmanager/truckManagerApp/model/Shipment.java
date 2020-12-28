package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "distance")
    private Float distance;
    @Column(name = "income")
    private Float income;
    @Column(name = "category")
    private String category;
    @Column(name = "departure_time")
    private Timestamp departureTime;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "departure_location_id")
    private Location departureLocation;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "arrival_location_id")
    private Location arrivalLocation;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "truck_id")
    private Truck truck;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipment shipment = (Shipment) o;
        return id.equals(shipment.id) &&
                distance.equals(shipment.distance) &&
                income.equals(shipment.income) &&
                category.equals(shipment.category) &&
                departureTime.equals(shipment.departureTime) &&
                departureLocation.equals(shipment.departureLocation) &&
                arrivalLocation.equals(shipment.arrivalLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, distance, income, category, departureTime, departureLocation, arrivalLocation);
    }
}
