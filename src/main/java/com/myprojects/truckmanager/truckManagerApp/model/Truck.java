package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    @Column(name = "category")
    private String category;
    @Column(name = "max_load")
    private Integer maxLoad;
    @Column(name = "status")
    private String status;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "truck_details_id")
    private TruckDetails details;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Truck truck = (Truck) o;
        return id.equals(truck.id) &&
                model.equals(truck.model) &&
                category.equals(truck.category) &&
                maxLoad.equals(truck.maxLoad) &&
                status.equals(truck.status) &&
                company.equals(truck.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, category, maxLoad, status, company);
    }
}
