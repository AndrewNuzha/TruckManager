package com.myprojects.truckmanager.truckManagerApp.service.impl;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.dto.ShipmentInfoDTO;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.repository.ShipmentRepository;
import com.myprojects.truckmanager.truckManagerApp.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceImplTest {

    @Mock
    private LocationAndTimeService locationAndTimeServiceMock;
    @Mock
    private TruckService truckServiceMock;
    @Mock
    private CompanyService companyServiceMock;
    @Mock
    private LogService logServiceMock;
    @Mock
    private ShipmentRepository shipmentRepositoryMock;
    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    private static Company company;
    private static Truck truck;
    private static List<Location> locations;
    private static Location starterLocation;
    private static Shipment shipment;

    @BeforeAll
    public static void initializeTestData() {
        starterLocation = new Location("Russia", "Saint-Petersburg", 59.939095, 30.315868);
        starterLocation.setId(1L);
        Location departureLocation = new Location("Russia", "Moscow", 55.755814, 37.617635);
        departureLocation.setId(2L);
        company = new Company("SomeName", 9999.99f);
        company.setId(1L);
        locations = new ArrayList<>();
        locations.add(starterLocation);
        locations.add(departureLocation);

        truck = new Truck();
        truck.setId(1L);
        truck.setModel("Scania RX100");
        truck.setCategory(TruckCategory.VAN.getCategory);
        truck.setMaxLoad(10);
        truck.setStatus(TruckStatus.AVAILABLE.getStatus);
        truck.setCategory(TruckCategory.VAN.getCategory);
        truck.setCompany(company);

        TruckDetails truckDetails = new TruckDetails();
        truckDetails.setMileage(0F);
        truckDetails.setMileageBeforeService(25000f);
        truckDetails.setFuelConsumption(0.52f);
        truckDetails.setCurrentLocation(starterLocation);
        truckDetails.setProductionYear(new Timestamp(new Date().getTime()));
        truck.setDetails(truckDetails);

        shipment = new Shipment();
        shipment.setCompany(company);
        shipment.setTruck(truck);
        shipment.setIncome(1000f);
        shipment.setDepartureLocation(starterLocation);
        shipment.setArrivalLocation(departureLocation);
        shipment.setDistance(90f);
        shipment.setDepartureTime(new Timestamp(new Date().getTime()));
    }

    @Test
    public void getAvailableShipmentOrdersTest() {
        when(locationAndTimeServiceMock.getAllLocations()).thenReturn(locations);
        when(locationAndTimeServiceMock.calculateDistance(any(Location.class), any(Location.class))).thenReturn(250f);

        List<NewShipmentDTO> availableShipmentOrders = shipmentService.getAvailableShipmentOrders(truck);

        Assertions.assertEquals(1, availableShipmentOrders.size());
        Assertions.assertEquals(company.getId(), availableShipmentOrders.get(0).getCompanyId());
        Assertions.assertEquals(truck.getId(), availableShipmentOrders.get(0).getTruckId());
        Assertions.assertEquals(1L, availableShipmentOrders.get(0).getDepartureLocationId());
        Assertions.assertEquals(2L, availableShipmentOrders.get(0).getArrivalLocationId());
        Assertions.assertEquals("Moscow", availableShipmentOrders.get(0).getArrivalLocationCity());
        Assertions.assertEquals(250f, availableShipmentOrders.get(0).getDistance());
        Assertions.assertEquals(212.5f, availableShipmentOrders.get(0).getIncome());
        Assertions.assertEquals("Trailer", availableShipmentOrders.get(0).getCategory());
    }

    @Test
    public void getAvailableShipmentOrdersNeedServiceTest() {
        Truck truckBeforeService = new Truck();
        truckBeforeService.setId(1L);
        truckBeforeService.setCompany(company);
        TruckDetails truckDetails = new TruckDetails();
        truckDetails.setMileageBeforeService(100f);
        truckDetails.setCurrentLocation(starterLocation);
        truckBeforeService.setDetails(truckDetails);
        ArgumentCaptor<String> truckStatusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> truckIdCaptor = ArgumentCaptor.forClass(Long.class);

        when(locationAndTimeServiceMock.getAllLocations()).thenReturn(locations);
        when(locationAndTimeServiceMock.calculateDistance(any(Location.class), any(Location.class))).thenReturn(500f);

        List<NewShipmentDTO> availableShipmentOrders = shipmentService.getAvailableShipmentOrders(truckBeforeService);

        Assertions.assertEquals(0, availableShipmentOrders.size());
        verify(truckServiceMock, times(1)).updateTruckStatus(truckStatusCaptor.capture(),
                truckIdCaptor.capture());
        Assertions.assertEquals(1L, truckIdCaptor.getValue());
        Assertions.assertEquals(TruckStatus.SERVICE.getStatus, truckStatusCaptor.getValue());
    }

    @Test
    public void prepareShipmentsDataTest() {
        List<Shipment> shipments = new ArrayList<>();
        shipments.add(shipment);
        ArgumentCaptor<Timestamp> departureTimeCaptor = ArgumentCaptor.forClass(Timestamp.class);
        ArgumentCaptor<Float> distanceCaptor = ArgumentCaptor.forClass(Float.class);

        List<ShipmentInfoDTO> shipmentInfoDTOS = shipmentService.prepareShipmentsData(shipments);

        Assertions.assertEquals(1, shipmentInfoDTOS.size());
        Assertions.assertEquals(1, shipmentInfoDTOS.get(0).getId());
        Assertions.assertEquals(locations.get(0).getCity(), shipmentInfoDTOS.get(0).getDepartureCity());
        Assertions.assertEquals(locations.get(1).getCity(), shipmentInfoDTOS.get(0).getArrivalCity());
        Assertions.assertEquals(1000f, shipmentInfoDTOS.get(0).getIncome());
        verify(locationAndTimeServiceMock, times(1))
                .convertTimestampToLocalDateTime(departureTimeCaptor.capture());
        verify(locationAndTimeServiceMock, times(1))
                .calculateShipmentArrivalTime(distanceCaptor.capture(), departureTimeCaptor.capture());
        Assertions.assertEquals(shipment.getDepartureTime(), departureTimeCaptor.getValue());
        Assertions.assertEquals(shipment.getDepartureTime(), departureTimeCaptor.getValue());
        Assertions.assertEquals(shipment.getDistance(), distanceCaptor.getValue());
    }

    @Test
    public void completeShipmentTest() {
        ArgumentCaptor<String> truckStatusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> truckIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Truck> truckCaptor = ArgumentCaptor.forClass(Truck.class);
        ArgumentCaptor<Float> passedDistanceCaptor = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Location> arrivalLocationCaptor = ArgumentCaptor.forClass(Location.class);
        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        ArgumentCaptor<Shipment> shipmentCaptor = ArgumentCaptor.forClass(Shipment.class);
        ArgumentCaptor<Long> shipmentIdCaptor = ArgumentCaptor.forClass(Long.class);

        shipmentService.completeShipment(shipment);

        verify(truckServiceMock, times(1)).updateTruckStatus(truckStatusCaptor.capture(),
                truckIdCaptor.capture());
        Assertions.assertEquals(shipment.getTruck().getId(), truckIdCaptor.getValue());
        Assertions.assertEquals(TruckStatus.AVAILABLE.getStatus, truckStatusCaptor.getValue());
        verify(truckServiceMock, times(1)).updateTruckMileage(truckCaptor.capture(),
                passedDistanceCaptor.capture());
        Assertions.assertEquals(shipment.getDistance(), passedDistanceCaptor.getValue());
        Assertions.assertEquals(shipment.getTruck(), truckCaptor.getValue());
        verify(truckServiceMock, times(1)).updateTruckLocation(arrivalLocationCaptor.capture(),
                truckIdCaptor.capture());
        Assertions.assertEquals(shipment.getArrivalLocation(), arrivalLocationCaptor.getValue());
        Assertions.assertEquals(shipment.getTruck().getId(), truckIdCaptor.getValue());
        verify(companyServiceMock, times(1)).updateBalance(companyCaptor.capture(), anyFloat());
        Assertions.assertEquals(shipment.getCompany(), companyCaptor.getValue());
        verify(logServiceMock, times(1)).writeShipmentCompletedLog(shipmentCaptor.capture());
        Assertions.assertEquals(shipment, shipmentCaptor.getValue());
        verify(shipmentRepositoryMock, times(1)).deleteById(shipmentIdCaptor.capture());
        verifyNoMoreInteractions(shipmentRepositoryMock);
    }
}