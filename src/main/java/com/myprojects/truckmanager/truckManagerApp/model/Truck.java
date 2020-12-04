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
@Table(name = "trucks")
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "model")
    private String model;
    @Column(name = "fuel_consumption")
    private Float fuelConsumption;
    @Column(name = "mileage")
    private Long mileage;
    @Column(name = "max_load")
    private Integer maxLoad;
    @Column(name = "production_year")
    private Timestamp productionYear;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id")
    private Company company;

    public Truck(String model, Float fuelConsumption, Long mileage, Integer maxLoad, Timestamp productionYear) {
        this.model = model;
        this.fuelConsumption = fuelConsumption;
        this.mileage = mileage;
        this.maxLoad = maxLoad;
        this.productionYear = productionYear;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, fuelConsumption, mileage, maxLoad, productionYear, company);
    }
}
