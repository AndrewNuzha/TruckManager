package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "truck_details")
public class TruckDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "mileage")
    private Float mileage;
    @Column(name = "mileage_before_service")
    private Float mileageBeforeService;
    @Column(name = "fuel_consumption")
    private Float fuelConsumption;
    @Column(name = "production_year")
    private Timestamp productionYear;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "current_location_id")
    private Location currentLocation;
}
