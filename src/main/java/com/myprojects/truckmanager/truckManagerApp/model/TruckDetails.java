package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
    @Column(name = "mileage_before_service")
    private Long mileageBeforeService;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_location_id")
    private Location currentLocation;
}
