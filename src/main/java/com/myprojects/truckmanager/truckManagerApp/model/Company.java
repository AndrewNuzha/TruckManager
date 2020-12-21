package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "companies", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "balance")
    private Float balance;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "company", fetch = FetchType.LAZY)
    private List<Truck> trucks;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "company", fetch = FetchType.LAZY)
    private List<Shipment> shipments;

    public Company(String name, Float balance) {
        this.name = name;
        this.balance = balance;
    }
}
