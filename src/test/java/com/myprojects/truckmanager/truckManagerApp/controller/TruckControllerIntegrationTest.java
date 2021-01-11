package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.dto.UserRegistrationDTO;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.repository.CompanyRepository;
import com.myprojects.truckmanager.truckManagerApp.repository.UserRepository;
import com.myprojects.truckmanager.truckManagerApp.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides tests for TruckController.
 * The tests depend on each other, so all tests must be run.
 *
 * @author Andrew Nuzha
 * @version 1.0
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class TruckControllerIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private TruckService truckService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private LocationAndTimeService locationAndTimeService;
    @Autowired
    private CompanyService companyService;
    private final static String userNickName = "Joker";
    private static UserRegistrationDTO userRegistrationDTO;

    @BeforeAll
    public static void initializeUserWithTrucks() {
        userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setFirstName("Alex");
        userRegistrationDTO.setLastName("NewMann");
        userRegistrationDTO.setEmail("testemail@mail.ru");
        userRegistrationDTO.setNickName(userNickName);
        userRegistrationDTO.setCompanyName("MyCompanyName");
        userRegistrationDTO.setPassword("password");
    }

    @Test
    @Order(1)
    public void checkTruckStatusTest() {
        locationAndTimeService.initializeLocationList();
        userService.save(userRegistrationDTO);
        User userFromDatabase = userService.findUserWithCompanyIdByNickName(userNickName);
        Truck truckWithDetailsOne = truckService.findTruckWithDetailsById(1L);
        Truck truckWithDetailsTwo = truckService.findTruckWithDetailsById(2L);
        NewShipmentDTO newShipmentDTO = new NewShipmentDTO();
        newShipmentDTO.setDistance(200f);
        newShipmentDTO.setDepartureLocationId(1L);
        newShipmentDTO.setArrivalLocationId(2L);
        newShipmentDTO.setIncome(1000f);
        newShipmentDTO.setCategory(TruckCategory.VAN.getCategory);
        newShipmentDTO.setTruckId(truckWithDetailsTwo.getId());
        newShipmentDTO.setCompanyId(userFromDatabase.getCompany().getId());

        Shipment newShipment = shipmentService.createNewShipment(newShipmentDTO);
        shipmentService.saveShipment(newShipment);
        Truck truckWithDetailsInTrip = truckService.findTruckWithDetailsById(2L);
        Assertions.assertEquals(TruckStatus.AVAILABLE.getStatus, truckWithDetailsOne.getStatus());
        Assertions.assertEquals(TruckStatus.TRIP.getStatus, truckWithDetailsInTrip.getStatus());
    }

    @Test
    @Order(2)
    public void completeShipment() {
        Shipment shipment = shipmentService.findShipmentsByCompanyId(1L).get(0);
        shipmentService.completeShipment(shipment);
    }

    @Test
    @Order(3)
    @Transactional
    public void serviceTruckTest() {
        Truck truckServiced = truckService.findTruckWithDetailsById(2L);
        User userWithCompanyIdByNickName = userService.findUserWithCompanyIdByNickName(userNickName);
        truckService.serviceTruck(truckServiced);
        Float balance = userWithCompanyIdByNickName.getCompany().getBalance();
        Assertions.assertEquals(25000, truckServiced.getDetails().getMileageBeforeService());
        Assertions.assertEquals(20944.62f, balance);
    }

    @Test
    @Order(4)
    @Transactional
    public void buyTruckTest() {
        NewTruck newTruck = truckService.getAllNewTrucks().get(0);
        Company company = userService.findUserWithCompanyIdByNickName(userNickName).getCompany();
        companyService.addNewTruckToCompany(1, company);
        Truck truck = company.getTrucks().get(2);
        Assertions.assertNotNull(truck);
        Assertions.assertEquals(truck.getModel(), newTruck.getModel());
        Assertions.assertEquals(truck.getMaxLoad(), newTruck.getMaxLoad());
        Assertions.assertEquals(truck.getDetails().getFuelConsumption(), newTruck.getFuelConsumption());
    }

    @Test
    @Order(5)
    @Transactional
    public void sellTruckSell() {
        User user = userService.findUserWithCompanyIdByNickName(userNickName);
        Company company = user.getCompany();
        Truck truck = truckService.findTruckWithDetailsById(1L);
        truckService.sellTruck(truck);

        Assertions.assertEquals(2, company.getTrucks().size());
        Assertions.assertEquals(40945.92f, company.getBalance());
    }
}