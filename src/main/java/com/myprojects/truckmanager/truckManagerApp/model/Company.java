package com.myprojects.truckmanager.truckManagerApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    public Company(String name, Long balance) {
        this.name = name;
        this.balance = balance;
    }
}
