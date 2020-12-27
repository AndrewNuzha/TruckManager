package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.dto.ShipmentInfoDTO;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.service.ShipmentService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(ShipmentController.class)
class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;
    @MockBean
    private TruckService truckServiceMock;
    @MockBean
    private ShipmentService shipmentServiceMock;
    @MockBean
    private IAuthenticationFacade authenticationFacadeMock;

    private final Authentication authentication = mock(Authentication.class);
    private static User userWithCompany;

    @BeforeAll
    public static void initializeUserWithCompanyAndShipments() {
        Company company = new Company();
        company.setId(1L);
        company.setName("ShipmentCompany");
        company.setBalance(9999.99f);
        userWithCompany = new User();
        userWithCompany.setNickName("Nickname");
        userWithCompany.setCompany(company);
    }

    @Test
    void showCreateShipmentForm() {
    }

    @Test
    void createNewShipment() {
    }

    @Test
    @WithMockUser(username = "admin")
    void showShipmentsFormTest() throws Exception {
        Location departureLocation = new Location("Russia", "Saint-Petersburg", 59.939095, 30.315868);
        Location arrivalLocation = new Location("Russia", "Moscow", 55.755814, 37.617635);
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusMinutes(10L);
        List<Shipment> shipments = new ArrayList<>();
        Shipment shipmentOne = new Shipment();
        shipmentOne.setId(1L);
        shipmentOne.setDistance(99.99f);
        shipmentOne.setIncome(999.22f);
        shipmentOne.setDepartureLocation(departureLocation);
        shipmentOne.setArrivalLocation(arrivalLocation);
        shipmentOne.setCompany(userWithCompany.getCompany());
        shipments.add(shipmentOne);
        List<ShipmentInfoDTO> shipmentInfoList = new ArrayList<>();
        ShipmentInfoDTO shipmentInfo = new ShipmentInfoDTO();
        shipmentInfo.setId(1);
        shipmentInfo.setIncome(shipmentOne.getIncome());
        shipmentInfo.setArrivalCity(arrivalLocation.getCity());
        shipmentInfo.setDepartureCity(departureLocation.getCity());
        shipmentInfo.setDepartureTime(departureTime);
        shipmentInfo.setArrivalTime(arrivalTime);
        shipmentInfoList.add(shipmentInfo);

        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompany.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName()))
                .thenReturn(userWithCompany);
        when(shipmentServiceMock.findShipmentsByCompanyId(userWithCompany.getCompany().getId())).thenReturn(shipments);
        when(shipmentServiceMock.getActualShipments(shipments)).thenReturn(shipments);
        when(shipmentServiceMock.prepareShipmentsData(shipments)).thenReturn(shipmentInfoList);

        mockMvc.perform(get("/shipments"))
                .andExpect(status().isOk())
                .andExpect(view().name("shipments"))
                .andExpect(model().attribute("balance", is(userWithCompany.getCompany().getBalance())))
                .andExpect(model().attribute("username", is(userWithCompany.getNickName())))
                .andExpect(model().attribute("shipments", hasSize(1)))
                .andExpect(model().attribute("shipments", hasItem(
                        allOf(
                                hasProperty("departureCity", is(departureLocation.getCity())),
                                hasProperty("arrivalCity", is(arrivalLocation.getCity())),
                                hasProperty("departureTime", is(departureTime)),
                                hasProperty("arrivalTime", is(arrivalTime))
                        )
                )));
    }
}