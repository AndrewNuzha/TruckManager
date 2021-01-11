package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.dto.ShipmentInfoDTO;
import com.myprojects.truckmanager.truckManagerApp.dto.UserRegistrationDTO;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.service.LocationAndTimeService;
import com.myprojects.truckmanager.truckManagerApp.service.ShipmentService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * Provides tests for ShipmentController.
 *
 * @author Andrew Nuzha
 * @version 1.0
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class ShipmentControllerIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private LocationAndTimeService locationAndTimeService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private TruckService truckService;
    private final static String userNickName = "Joker";
    private static UserRegistrationDTO userRegistrationDTO;
    private static NewShipmentDTO newShipmentDTO;
    private final static Location departureLocation = new Location("Russia", "Moscow",
            55.755814, 37.617635);

    @BeforeAll
    public static void initializeUserAndNewShipmentDTO() {
        userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setFirstName("Alex");
        userRegistrationDTO.setLastName("NewMann");
        userRegistrationDTO.setEmail("testemail@mail.ru");
        userRegistrationDTO.setNickName(userNickName);
        userRegistrationDTO.setCompanyName("MyCompanyName");
        userRegistrationDTO.setPassword("password");

        newShipmentDTO = new NewShipmentDTO();
        newShipmentDTO.setTruckId(1L);
        newShipmentDTO.setCompanyId(1L);
        newShipmentDTO.setCategory("Some category");
        newShipmentDTO.setDepartureLocationId(1L);
        newShipmentDTO.setArrivalLocationId(2L);
        newShipmentDTO.setArrivalLocationCity(departureLocation.getCity());
        newShipmentDTO.setDistance(100f);
        newShipmentDTO.setCategory(TruckCategory.VAN.getCategory);
        newShipmentDTO.setIncome(1000f);
    }

    @Test
    @Order(1)
    public void showCreateShipmentFormTest() {
        locationAndTimeService.initializeLocationList();
        userService.save(userRegistrationDTO);
        Truck truck = truckService.findTruckWithDetailsById(1L);
        List<NewShipmentDTO> availableShipmentOrders = shipmentService.getAvailableShipmentOrders(truck);

        Assertions.assertEquals(2, availableShipmentOrders.size());
        Assertions.assertEquals(1L, availableShipmentOrders.get(0).getTruckId());
        Assertions.assertEquals(departureLocation.getCity(), availableShipmentOrders.get(0).getArrivalLocationCity());
        Assertions.assertEquals(1L, availableShipmentOrders.get(1).getTruckId());
        Assertions.assertEquals("Kazan", availableShipmentOrders.get(1).getArrivalLocationCity());
    }

    @Test
    @Order(2)
    public void createShipmentTest() {
        Shipment shipment = shipmentService.createNewShipment(newShipmentDTO);
        shipmentService.saveShipment(shipment);

        Truck truckInShipment = truckService.findTruckWithDetailsById(1L);
        Assertions.assertEquals(TruckStatus.TRIP.getStatus, truckInShipment.getStatus());
        Assertions.assertEquals(1, shipmentService.findShipmentsByCompanyId(1L).size());
    }

    @Test
    public void showShipmentsFormTest() {
        List<Shipment> companyShipments = shipmentService.findShipmentsByCompanyId(1L);
        List<Shipment> actualShipments = shipmentService.getActualShipments(companyShipments);
        List<ShipmentInfoDTO> companyShipmentsInfo = shipmentService.prepareShipmentsData(actualShipments);

        Assertions.assertEquals(1, actualShipments.size());
        Assertions.assertEquals(newShipmentDTO.getTruckId(), actualShipments.get(0).getTruck().getId());
        Assertions.assertEquals(newShipmentDTO.getCompanyId(), actualShipments.get(0).getCompany().getId());
        Assertions.assertEquals(newShipmentDTO.getDistance(), actualShipments.get(0).getDistance());
        Assertions.assertEquals(newShipmentDTO.getIncome(), actualShipments.get(0).getIncome());
        Assertions.assertEquals(newShipmentDTO.getArrivalLocationId(),
                actualShipments.get(0).getArrivalLocation().getId());
        Assertions.assertEquals(newShipmentDTO.getDepartureLocationId(),
                actualShipments.get(0).getDepartureLocation().getId());

        Assertions.assertEquals(1, companyShipmentsInfo.size());
        Assertions.assertEquals(newShipmentDTO.getIncome(), companyShipmentsInfo.get(0).getIncome());
        Assertions.assertEquals(newShipmentDTO.getArrivalLocationCity(), companyShipmentsInfo.get(0).getArrivalCity());
        Assertions.assertEquals("Saint-Petersburg", companyShipmentsInfo.get(0).getDepartureCity());
    }

}