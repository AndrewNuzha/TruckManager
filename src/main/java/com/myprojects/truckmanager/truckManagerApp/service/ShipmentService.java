package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.model.Shipment;

public interface ShipmentService {

    Shipment createNewShipment(NewShipmentDTO newShipmentDTO);

    Shipment save(Shipment shipment);

}
