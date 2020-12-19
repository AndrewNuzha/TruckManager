package com.myprojects.truckmanager.truckManagerApp.service.impl;

import com.myprojects.truckmanager.truckManagerApp.dto.ShipmentInfoDTO;
import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.repository.ShipmentRepository;
import com.myprojects.truckmanager.truckManagerApp.service.CompanyService;
import com.myprojects.truckmanager.truckManagerApp.service.LocationService;
import com.myprojects.truckmanager.truckManagerApp.service.ShipmentService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
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
    @Transactional(readOnly = true)
    public List<Shipment> findShipmentsByCompanyId(Long id) {
        return shipmentRepository.findShipmentByCompany_Id(id);
    }

    @Override
    public List<ShipmentInfoDTO> prepareShipmentsData(List<Shipment> shipments) {
        int counter = 1;
        List<ShipmentInfoDTO> shipmentsInfoList = new ArrayList<>();
        for (Shipment shipment : shipments) {
            ShipmentInfoDTO shipmentInfo = new ShipmentInfoDTO();
            shipmentInfo.setId(counter);
            shipmentInfo.setDepartureCity(shipment.getDepartureLocation().getCity());
            shipmentInfo.setArrivalCity(shipment.getArrivalLocation().getCity());
            shipmentInfo.setIncome(shipment.getIncome());
            shipmentInfo.setDepartureTime(locationService.convertTimestampToLocalDateTime(shipment.getDepartureTime()));
            shipmentInfo.setArrivalTime(locationService.calculateShipmentArrivalTime(shipment.getDistance(),
                    shipment.getDepartureTime()));
            shipmentInfo.setEstimatedDistance(locationService.calculateDistanceProgress(shipment.getDistance(),
                    shipment.getDepartureTime()));
            shipmentsInfoList.add(shipmentInfo);
            counter++;
        }
        return shipmentsInfoList;
    }

    @Override
    public List<Shipment> getActualShipments(List<Shipment> shipments) {
        List<Shipment> actualShipments = new ArrayList<>();
        for (int i = 0; i < shipments.size(); i++) {
            Shipment shipment = shipments.get(i);
            if (locationService.isShipmentCompleted(shipment.getDistance(), shipment.getDepartureTime())) {
                completeShipment(shipment);
            } else {
                actualShipments.add(shipment);
            }
        }
        return actualShipments;
    }

    public void completeShipment(Shipment shipment) {
        truckService.updateTruckStatus(TruckStatus.AVAILABLE.getStatus, shipment.getTruck().getId());
        truckService.updateTruckMileage(shipment.getTruck().getMileage() + 1000,
                shipment.getTruck().getId());
        Company company = shipment.getCompany();
        company.setBalance(company.getBalance() + shipment.getIncome());

        deleteShipment(shipment.getId());
    }

    @Override
    @Transactional
    public void deleteShipment(Long id) {
        shipmentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Shipment save(Shipment shipment) {
        truckService.updateTruckStatus(TruckStatus.TRIP.getStatus, shipment.getTruck().getId());
        return shipmentRepository.save(shipment);
    }
}
