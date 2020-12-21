package com.myprojects.truckmanager.truckManagerApp.repository;

import com.myprojects.truckmanager.truckManagerApp.model.Location;
import com.myprojects.truckmanager.truckManagerApp.model.TruckDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckDetailsRepository extends JpaRepository<TruckDetails, Long> {

    @Modifying
    @Query("update TruckDetails details set details.mileage = :mileage, " +
            "details.mileageBeforeService = :mileageBeforeService where details.id = :id")
    void updateTruckMileage(@Param("mileage") Float mileage, @Param("mileageBeforeService") Float mileageBeforeService,
                            @Param("id") Long id);

    @Modifying
    @Query("update TruckDetails details set details.currentLocation = :location where details.id = :id")
    void updateTruckLocation(@Param("location") Location location, @Param("id") Long id);
}
