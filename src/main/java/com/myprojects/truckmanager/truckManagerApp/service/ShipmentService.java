package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.dto.ShipmentInfoDTO;
import com.myprojects.truckmanager.truckManagerApp.model.Shipment;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;

import java.util.List;

public interface ShipmentService {

    List<NewShipmentDTO> getAvailableShipmentOrders(Truck truck);

    Shipment createNewShipment(NewShipmentDTO newShipmentDTO);

    Shipment saveShipment(Shipment shipment);

    void completeShipment(Shipment shipment);

    List<Shipment> findShipmentsByCompanyId(Long id);

    List<ShipmentInfoDTO> prepareShipmentsData(List<Shipment> shipments);

    List<Shipment> getActualShipments(List<Shipment> shipments);

    void deleteShipment(Long id);

}
