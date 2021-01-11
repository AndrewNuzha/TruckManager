package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "company", fetch = FetchType.LAZY)
    private List<Truck> trucks;

    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "company", fetch = FetchType.LAZY)
    private List<Shipment> shipments;

    @OneToOne(cascade = CascadeType.MERGE, mappedBy = "company", fetch = FetchType.EAGER)
    private User user;

    public Company(String name, Float balance) {
        this.name = name;
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return id.equals(company.id) &&
                name.equals(company.name) &&
                balance.equals(company.balance) &&
                user.equals(company.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, balance, user);
    }
}
