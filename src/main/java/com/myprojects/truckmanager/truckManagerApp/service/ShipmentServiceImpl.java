package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.model.Shipment;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.sql.Timestamp;
import java.util.Date;

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
        shipment.setDepartureTime(new Timestamp(new Date().getTime()));
        shipment.setDepartureLocation(locationService.getLocationById(newShipmentDTO.getDepartureLocationId()));
        shipment.setArrivalLocation(locationService.getLocationById(newShipmentDTO.getArrivalLocationId()));
        shipment.setTruck(truckService.findTruckById(newShipmentDTO.getTruckId()));
        shipment.setCompany(companyService.findCompanyById(newShipmentDTO.getCompanyId()));

        return shipment;
    }

    @Override
    @Transactional
    public Shipment save(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }
}
