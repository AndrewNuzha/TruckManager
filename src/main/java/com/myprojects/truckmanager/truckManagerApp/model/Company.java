package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Long balance;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "company", fetch = FetchType.LAZY)
    private List<Truck> trucks;

    public Company(String name, Long balance) {
        this.name = name;
        this.balance = balance;
    }
}
