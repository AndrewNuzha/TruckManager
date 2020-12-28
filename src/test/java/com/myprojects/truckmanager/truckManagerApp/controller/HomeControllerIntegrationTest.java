package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.dto.UserRegistrationDTO;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.service.*;
import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class HomeControllerIntegrationTest {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserService userService;
    @Autowired
    private TruckService truckService;
    @Autowired
    private LogService logService;
    @Autowired
    private LocationAndTimeService locationAndTimeService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private final static String userNickName = "Joker";
    private static UserRegistrationDTO userRegistrationDTO;

    @BeforeAll
    public static void initializeUserDTO() {
        userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setFirstName("Alex");
        userRegistrationDTO.setLastName("NewMann");
        userRegistrationDTO.setEmail("testemail@mail.ru");
        userRegistrationDTO.setNickName(userNickName);
        userRegistrationDTO.setCompanyName("MyCompanyName");
        userRegistrationDTO.setPassword("password");
    }

    @Test
    public void saveUserTest() {
        locationAndTimeService.initializeLocationList();
        userService.save(userRegistrationDTO);

        User userFromDatabase = userService.findUserWithCompanyByNickName(userRegistrationDTO.getNickName());

        Assertions.assertNotNull(userFromDatabase);
        Assertions.assertEquals(userFromDatabase.getNickName(), userRegistrationDTO.getNickName());
        Assertions.assertEquals(userFromDatabase.getFirstName(), userRegistrationDTO.getFirstName());
        Assertions.assertEquals(userFromDatabase.getLastName(), userRegistrationDTO.getLastName());
        Assertions.assertEquals(userFromDatabase.getEmail(), userRegistrationDTO.getEmail());
        Assertions.assertEquals(userFromDatabase.getCompany().getName(), userRegistrationDTO.getCompanyName());
        Assertions.assertTrue(passwordEncoder.matches(userRegistrationDTO.getPassword(),
                userFromDatabase.getPassword()));
        Assertions.assertEquals(userFromDatabase.getCompany().getBalance(), 20000f);
        Assertions.assertEquals(userFromDatabase.getCompany().getTrucks().size(), 2);
    }

    @Test
    @WithMockUser
    public void showLogsForm() {
        Company company = companyService.findCompanyById(1L);
        Shipment shipment = new Shipment();
        shipment.setArrivalLocation(new Location("Russia", "Saint-Petersburg", 59.939095, 30.315868));
        shipment.setDepartureLocation(new Location("Russia", "Moscow", 55.755814, 37.617635));
        shipment.setIncome(199.99f);
        shipment.setCompany(company);
        String logText = String.format("Shipment from %s to %s completed. Income %f received",
                shipment.getDepartureLocation().getCity(), shipment.getArrivalLocation().getCity(),
                Precision.round(shipment.getIncome(), 2));

        logService.writeShipmentCompletedLog(shipment);
        User userFromDatabase = userService.findUserWithCompanyAndLogsByNickName(userNickName);

        Assertions.assertEquals(userFromDatabase.getNickName(), userRegistrationDTO.getNickName());
        Assertions.assertEquals(userFromDatabase.getFirstName(), userRegistrationDTO.getFirstName());
        Assertions.assertEquals(userFromDatabase.getLastName(), userRegistrationDTO.getLastName());
        Assertions.assertEquals(userFromDatabase.getEmail(), userRegistrationDTO.getEmail());
        Assertions.assertEquals(userFromDatabase.getCompany().getName(), userRegistrationDTO.getCompanyName());
        Assertions.assertTrue(passwordEncoder.matches(userRegistrationDTO.getPassword(),
                userFromDatabase.getPassword()));
        Assertions.assertEquals(userFromDatabase.getCompany().getBalance(), 20000f);
        Assertions.assertEquals(userFromDatabase.getLogs().size(), 1);
        Log log = userFromDatabase.getLogs().get(0);
        Assertions.assertEquals(log.getLogText(), logText);
    }
}