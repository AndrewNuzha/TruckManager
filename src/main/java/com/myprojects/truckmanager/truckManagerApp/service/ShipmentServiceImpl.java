package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private LocationService locationService;
    @Autowired
    private TruckService truckService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private ShipmentRepository shipmentRepository;

    @Override
    public Shipment createNewShipment(NewShipmentDTO newShipmentDTO) {

        Shipment shipment = new Shipment();
        shipment.setCategory(newShipmentDTO.getCategory());
        shipment.setDistance(newShipmentDTO.getDistance());
        shipment.setIncome(newShipmentDTO.getIncome());
        shipment.setDepartureTime(new Timestamp(new Date().getTime()));
        shipment.setDepartureLocation(locationService.getLocationById(newShipmentDTO.getDepartureLocationId()));
        shipment.setArrivalLocation(locationService.getLocationById(newShipmentDTO.getArrivalLocationId()));
        shipment.setTruck(truckService.findTruckById(newShipmentDTO.getTruckId()));
        shipment.setCompany(companyService.findCompanyById(newShipmentDTO.getCompanyId()));

        return shipment;
    }

    @Override
    public List<NewShipmentDTO> getAvailableShipmentOrders(Truck truck) {
        List<NewShipmentDTO> availableShipments = new ArrayList<>();
        List<Location> allLocationsForTruck = locationService.getAllLocations().stream()
                .filter(loc -> !loc.getCity().equals(truck.getDetails()
                        .getCurrentLocation().getCity()))
                .collect(Collectors.toList());
        for (Location location : allLocationsForTruck) {
            float distance = (locationService.calculateDistance(truck.getDetails().getCurrentLocation(), location));
            if (distance < truck.getDetails().getMileageBeforeService()) {
                NewShipmentDTO shipmentDTO = new NewShipmentDTO();
                shipmentDTO.setTruckId(truck.getId());
                shipmentDTO.setCompanyId(truck.getCompany().getId());
                shipmentDTO.setDepartureLocationId(truck.getDetails().getCurrentLocation().getId());
                shipmentDTO.setArrivalLocationId(location.getId());
                shipmentDTO.setArrivalLocationCity(location.getCity());
                shipmentDTO.setDistance(distance);
                shipmentDTO.setCategory(TruckCategory.VAN.getCategory);
                shipmentDTO.setIncome(1600); //TODO add price calculation service
                availableShipments.add(shipmentDTO);
            }
        }
        return availableShipments;
    }

    @Override
    @Transactional
    public Shipment save(Shipment shipment) {
        truckService.updateTruckStatus(TruckStatus.TRIP.getStatus, shipment.getTruck().getId());
        return shipmentRepository.save(shipment);
    }
}
