package com.myprojects.truckmanager.truckManagerApp.repository;

import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {
}
