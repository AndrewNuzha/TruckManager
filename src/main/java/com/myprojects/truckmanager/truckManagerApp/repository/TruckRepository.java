package com.myprojects.truckmanager.truckManagerApp.repository;

import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {

    @EntityGraph(attributePaths = {"details", "details.currentLocation"})
    Truck findTruckById(Long id);

    @Modifying
    @Query("update Truck truck set truck.status = :status where truck.id = :id")
    void updateTruckStatus(@Param("status") String status, @Param("id") Long id);

}
