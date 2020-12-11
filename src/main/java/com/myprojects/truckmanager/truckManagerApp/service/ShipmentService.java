package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.model.Shipment;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;

import java.util.List;

public interface ShipmentService {

    List<NewShipmentDTO> getAvailableShipmentOrders(Truck truck);

    Shipment createNewShipment(NewShipmentDTO newShipmentDTO);

    Shipment save(Shipment shipment);

}
