package com.myprojects.truckmanager.truckManagerApp.service.impl;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.dto.ShipmentInfoDTO;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.repository.ShipmentRepository;
import com.myprojects.truckmanager.truckManagerApp.service.*;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the shipment logic
 *
 * @author Andrew Nuzha
 * @version 1.0
 */
@Service
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private LocationAndTimeService locationAndTimeService;
    @Autowired
    private TruckService truckService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private LogService logService;
    @Autowired
    private ShipmentRepository shipmentRepository;

    /**
     * initializes {@link Shipment} object
     * from DTO {@code newShipmentDTO}.
     *
     * @param newShipmentDTO DTO object with the necessary information to create shipment
     * @return initialized shipment
     */
    @Override
    public Shipment createNewShipment(NewShipmentDTO newShipmentDTO) {

        Shipment shipment = new Shipment();
        shipment.setCategory(newShipmentDTO.getCategory());
        shipment.setDistance(newShipmentDTO.getDistance());
        shipment.setIncome(newShipmentDTO.getIncome());
        shipment.setDepartureTime(new Timestamp(new Date().getTime()));
        shipment.setDepartureLocation(locationAndTimeService.getLocationById(newShipmentDTO.getDepartureLocationId()));
        shipment.setArrivalLocation(locationAndTimeService.getLocationById(newShipmentDTO.getArrivalLocationId()));
        shipment.setTruck(truckService.findTruckWithDetailsById(newShipmentDTO.getTruckId()));
        shipment.setCompany(companyService.findCompanyById(newShipmentDTO.getCompanyId()));

        return shipment;
    }

    /**
     * Creates a list of available shipments for a truck {@code truck}.
     * If the truck doesn't require service, a list of available shipments will be created.
     * If the truck needs a service and cannot participate any shipments, truck status changes
     * and available shipments list is empty.
     *
     * @param truck available shipments based truck
     * @return list of available shipments
     */
    @Override
    public List<NewShipmentDTO> getAvailableShipmentOrders(Truck truck) {
        List<NewShipmentDTO> availableShipments = new ArrayList<>();
        List<Location> allLocationsForTruck = locationAndTimeService.getAllLocations().stream()
                .filter(loc -> !loc.getCity().equals(truck.getDetails().getCurrentLocation().getCity()))
                .collect(Collectors.toList());
        for (Location location : allLocationsForTruck) {
            float distance = (locationAndTimeService.calculateDistance(truck.getDetails().getCurrentLocation(),
                    location));
            if (distance < truck.getDetails().getMileageBeforeService()) {
                NewShipmentDTO shipmentDTO = initializeNewShipmentDTO(truck, location, distance);
                availableShipments.add(shipmentDTO);
            } else {
                truckService.updateTruckStatus(TruckStatus.SERVICE.getStatus, truck.getId());
            }
        }
        return availableShipments;
    }

    /**
     * Returns a list of shipments for a company.
     *
     * @param id company id
     * @return list of {@link Shipment}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Shipment> findShipmentsByCompanyId(Long id) {
        return shipmentRepository.findShipmentByCompany_Id(id);
    }

    /**
     * Returns a list of {@link com.myprojects.truckmanager.truckManagerApp.dto.ShipmentInfoDTO} based on
     * list of {@link com.myprojects.truckmanager.truckManagerApp.model.Shipment}.
     *
     * @param shipments list of shipments
     * @return list of shipment info dto's
     */
    @Override
    public List<ShipmentInfoDTO> prepareShipmentsData(List<Shipment> shipments) {
        int counter = 1;
        List<ShipmentInfoDTO> shipmentsInfoList = new ArrayList<>();
        for (Shipment shipment : shipments) {
            ShipmentInfoDTO shipmentInfoDTO = initializeShipmentInfoDTO(counter, shipment);
            shipmentsInfoList.add(shipmentInfoDTO);
            counter++;
        }
        return shipmentsInfoList;
    }

    /**
     * Defines a list of current shipments. If the shipment is completed,
     * then the method {@link #completeShipment(Shipment)} is called.
     * Otherwise, the shipment will be added to the list of current shipments.
     *
     * @param shipments list of shipments
     * @return list of current shipments
     */
    @Override
    public List<Shipment> getActualShipments(List<Shipment> shipments) {
        List<Shipment> actualShipments = new ArrayList<>();
        for (Shipment shipment : shipments) {
            if (locationAndTimeService.isShipmentCompleted(shipment.getDistance(), shipment.getDepartureTime())) {
                completeShipment(shipment);
            } else {
                actualShipments.add(shipment);
            }
        }
        return actualShipments;
    }

    /**
     * Removes shipment {@link Shipment} from database.
     *
     * @param id shipment id
     */
    @Override
    @Transactional
    public void deleteShipment(Long id) {
        shipmentRepository.deleteById(id);
    }

    /**
     * Saves shipment object in database, changes shipment-truck status.
     *
     * @param shipment new shipment
     * @return saved shipment
     */
    @Override
    @Transactional
    public Shipment saveShipment(Shipment shipment) {
        truckService.updateTruckStatus(TruckStatus.TRIP.getStatus, shipment.getTruck().getId());
        return shipmentRepository.save(shipment);
    }

    /**
     * Completes {@link Shipment shipment}, changes truck status, truck mileage, truck mileage before service,
     * company balance.
     *
     * @param shipment shipment to be completed
     */
    @Override
    @Transactional
    public void completeShipment(Shipment shipment) {
        truckService.updateTruckStatus(TruckStatus.AVAILABLE.getStatus, shipment.getTruck().getId());
        truckService.updateTruckMileage(shipment.getTruck(), shipment.getDistance());
        truckService.updateTruckLocation(shipment.getArrivalLocation(), shipment.getTruck().getId());
        companyService.updateBalance(shipment.getCompany(), calculateShipmentIncome(shipment));
        logService.writeShipmentCompletedLog(shipment);
        deleteShipment(shipment.getId());
    }

    /**
     * Initializes new {@link ShipmentInfoDTO} object.
     *
     * @param counter  counter to set the shipment identifier
     * @param shipment shipment from DB
     * @return initialized shipment info object
     */
    private ShipmentInfoDTO initializeShipmentInfoDTO(int counter, Shipment shipment) {
        ShipmentInfoDTO shipmentInfoDTO = new ShipmentInfoDTO();
        shipmentInfoDTO.setId(counter);
        shipmentInfoDTO.setDepartureCity(shipment.getDepartureLocation().getCity());
        shipmentInfoDTO.setArrivalCity(shipment.getArrivalLocation().getCity());
        shipmentInfoDTO.setIncome(shipment.getIncome());
        shipmentInfoDTO.setDepartureTime(locationAndTimeService
                .convertTimestampToLocalDateTime(shipment.getDepartureTime()));
        shipmentInfoDTO.setArrivalTime(locationAndTimeService.calculateShipmentArrivalTime(shipment.getDistance(),
                shipment.getDepartureTime()));
        return shipmentInfoDTO;
    }

    /**
     * Initializes new {@link NewShipmentDTO} object.
     *
     * @param truck    truck performing shipment
     * @param location shipment arrival location
     * @param distance shipment distance
     * @return initialized shipmentDTO
     */
    private NewShipmentDTO initializeNewShipmentDTO(Truck truck, Location location, float distance) {
        NewShipmentDTO shipmentDTO = new NewShipmentDTO();
        shipmentDTO.setTruckId(truck.getId());
        shipmentDTO.setCompanyId(truck.getCompany().getId());
        shipmentDTO.setDepartureLocationId(truck.getDetails().getCurrentLocation().getId());
        shipmentDTO.setArrivalLocationId(location.getId());
        shipmentDTO.setArrivalLocationCity(location.getCity());
        shipmentDTO.setDistance(distance);
        shipmentDTO.setCategory(TruckCategory.VAN.getCategory);
        shipmentDTO.setIncome(calculateShipmentCost(distance, truck.getCategory(), truck.getMaxLoad()));
        return shipmentDTO;
    }

    /**
     * Calculates shipment income according to shipment spendings.
     * Fuel consumption and distance are taken into calculation.
     *
     * @param shipment shipment to calculate income
     * @return final shipment income.
     */
    private Float calculateShipmentIncome(Shipment shipment) {
        float consumedFuel = shipment.getTruck().getDetails().getFuelConsumption() * shipment.getDistance();
        float shipmentExpenses = consumedFuel * 0.52f;
        return Precision.round(shipment.getIncome() - shipmentExpenses, 2);
    }

    /**
     * Calculates shipment total income.
     * Distance, shipment category and maximum load of truck are taken into income calculation.
     *
     * @param distance shipment distance
     * @param category shipment category
     * @param maxLoad  maximum load of truck
     * @return shipment total income
     */
    private Float calculateShipmentCost(float distance, String category, Integer maxLoad) {
        if (category.equals(TruckCategory.VAN.getCategory)) {
            return Precision.round(0.085F * distance * maxLoad, 2);
        } else if (category.equals(TruckCategory.CONTAINER.getCategory)) {
            return Precision.round(0.1F * distance * maxLoad, 2);
        } else {
            return 0F;
        }
    }
}
