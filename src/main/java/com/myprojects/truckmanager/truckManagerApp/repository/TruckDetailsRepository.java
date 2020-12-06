package com.myprojects.truckmanager.truckManagerApp.repository;

import com.myprojects.truckmanager.truckManagerApp.model.TruckDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckDetailsRepository extends JpaRepository<TruckDetails, Long> {
}
