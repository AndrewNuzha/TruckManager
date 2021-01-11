package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.dto.TruckDetailsDTO;
import com.myprojects.truckmanager.truckManagerApp.exception_handler.NoSuchTruckException;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.service.CompanyService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TruckController.class)
class TruckControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;
    @MockBean
    private TruckService truckServiceMock;
    @MockBean
    private CompanyService companyService;
    @MockBean
    private IAuthenticationFacade authenticationFacadeMock;

    private final Authentication authentication = mock(Authentication.class);
    private static Company company;
    private static User userWithCompanyAndTrucks;
    private static List<NewTruck> newTruckList;

    @BeforeAll
    public static void initializeUserAndCompanyWithTrucks() {
        List<Truck> truckList = new ArrayList<>();
        Truck truckOne = initializeTestTruckWithDetailsById(1L);
        Truck truckTwo = initializeTestTruckWithDetailsById(2L);
        truckList.add(truckOne);
        truckList.add(truckTwo);
        company = new Company();
        company.setId(1L);
        company.setName("ShipmentCompany");
        company.setBalance(9999.99f);
        company.setTrucks(truckList);
        userWithCompanyAndTrucks = new User();
        userWithCompanyAndTrucks.setNickName("Nickname");
        userWithCompanyAndTrucks.setCompany(company);

        newTruckList = new ArrayList<>();
        newTruckList.add(new NewTruck(1, "Scania RX100", 10, 0.52f, 15000));
        newTruckList.add(new NewTruck(2, "MAN 818", 20, 0.48f, 20000));
    }

    public static Truck initializeTestTruckWithDetailsById(Long id) {
        Location location = new Location("Russia", "Saint-Petersburg", 59.939095, 30.315868);
        TruckDetails truckDetails = new TruckDetails(0f, 25000f, 0.52f,
                new Timestamp(new Date().getTime()), location);
        Truck truck = new Truck();
        truck.setId(id);
        truck.setModel("Scania RX100");
        truck.setMaxLoad(10);
        truck.setCategory(TruckCategory.VAN.getCategory);
        truck.setStatus(TruckStatus.AVAILABLE.getStatus);
        truck.setDetails(truckDetails);
        return truck;
    }

    @Test
    @WithMockUser(username = "admin")
    void showTrucksFormTest() throws Exception {
        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompanyAndTrucks.getNickName());
        when(userServiceMock.findUserWithCompanyByNickName(authentication.getName()))
                .thenReturn(userWithCompanyAndTrucks);

        mockMvc.perform(get("/trucks"))
                .andExpect(status().isOk())
                .andExpect(view().name("trucks"))
                .andExpect(model().attribute("balance", is(userWithCompanyAndTrucks.getCompany().getBalance())))
                .andExpect(model().attribute("username", is(userWithCompanyAndTrucks.getNickName())))
                .andExpect(model().attribute("trucks", hasSize(2)))
                .andExpect(model().attribute("trucks", hasItem(
                        allOf(
                                hasProperty("model", is("Scania RX100")),
                                hasProperty("maxLoad", is(10)),
                                hasProperty("category", is(TruckCategory.VAN.getCategory)),
                                hasProperty("status", is(TruckStatus.AVAILABLE.getStatus))
                        )
                )))
                .andExpect(model().attribute("trucks", hasItem(
                        allOf(
                                hasProperty("model", is("Scania RX100")),
                                hasProperty("maxLoad", is(10)),
                                hasProperty("category", is(TruckCategory.VAN.getCategory)),
                                hasProperty("status", is(TruckStatus.AVAILABLE.getStatus))
                        )
                )));

        ArgumentCaptor<String> nickNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(userServiceMock, times(1))
                .findUserWithCompanyByNickName(nickNameCaptor.capture());
        Assertions.assertEquals(userWithCompanyAndTrucks.getNickName(), nickNameCaptor.getValue());
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    @WithMockUser(username = "admin")
    void showTruckStoreFormTest() throws Exception {
        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompanyAndTrucks.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName()))
                .thenReturn(userWithCompanyAndTrucks);
        when(truckServiceMock.getAllNewTrucks()).thenReturn(newTruckList);

        mockMvc.perform(get("/truckstore"))
                .andExpect(status().isOk())
                .andExpect(view().name("truckstore"))
                .andExpect(model().attribute("balance", is(userWithCompanyAndTrucks.getCompany().getBalance())))
                .andExpect(model().attribute("username", is(userWithCompanyAndTrucks.getNickName())))
                .andExpect(model().attribute("trucks", hasSize(2)))
                .andExpect(model().attribute("trucks", hasItem(
                        allOf(
                                hasProperty("model", is("Scania RX100")),
                                hasProperty("maxLoad", is(10)),
                                hasProperty("fuelConsumption", is(0.52f)),
                                hasProperty("price", is(15000))
                        )
                )))
                .andExpect(model().attribute("trucks", hasItem(
                        allOf(
                                hasProperty("model", is("MAN 818")),
                                hasProperty("maxLoad", is(20)),
                                hasProperty("fuelConsumption", is(0.48f)),
                                hasProperty("price", is(20000))
                        )
                )));

        ArgumentCaptor<String> nickNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(userServiceMock, times(1))
                .findUserWithCompanyIdByNickName(nickNameCaptor.capture());
        Assertions.assertEquals(userWithCompanyAndTrucks.getNickName(), nickNameCaptor.getValue());
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    @WithMockUser(username = "admin")
    void getTruckDetailsTest() throws Exception {
        Long truckId = 1L;
        Truck truckWithDetails = initializeTestTruckWithDetailsById(truckId);
        TruckDetailsDTO truckDetailsDTO = new TruckDetailsDTO(truckWithDetails.getId(),
                truckWithDetails.getDetails().getMileage(), truckWithDetails.getDetails().getMileageBeforeService(),
                truckWithDetails.getDetails().getFuelConsumption(), "Russia , Saint-Petersburg");

        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompanyAndTrucks.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName()))
                .thenReturn(userWithCompanyAndTrucks);
        when(truckServiceMock.findTruckWithDetailsById(truckId)).thenReturn(truckWithDetails);
        when(truckServiceMock.doesCompanyContainTruck(truckWithDetails, userWithCompanyAndTrucks.getCompany()
                .getId())).thenReturn(true);
        when(truckServiceMock.prepareTruckDetails(truckWithDetails)).thenReturn(truckDetailsDTO);

        mockMvc.perform(get("/truck-details/{id}", truckId))
                .andExpect(status().isOk())
                .andExpect(content().json("{'truckId':1}"))
                .andExpect(content().json("{'mileage':0.0}"))
                .andExpect(content().json("{'mileageBeforeService':25000.0}"))
                .andExpect(content().json("{'fuelConsumption':0.52}"))
                .andExpect(content().json("{'currentLocationText':'Russia , Saint-Petersburg'}"));

        ArgumentCaptor<Long> truckIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(truckServiceMock, times(1)).findTruckWithDetailsById(truckIdCaptor.capture());
        Assertions.assertEquals(truckId, truckIdCaptor.getValue());
    }

    @Test
    @WithMockUser(username = "admin")
    void getTruckDetailsWithExceptionBecauseNoTruckTest() throws Exception {
        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompanyAndTrucks.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName()))
                .thenReturn(userWithCompanyAndTrucks);
        when(truckServiceMock.findTruckWithDetailsById(anyLong())).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(get("/truck-details/{id}", 1L))
                .andExpect(status().isNotFound()).andReturn();

        Exception resolvedException = mvcResult.getResolvedException();
        Assertions.assertEquals("There is no truck with ID=1", resolvedException.getMessage());
        Assertions.assertTrue(resolvedException instanceof NoSuchTruckException);

    }

    @Test
    @WithMockUser(username = "admin")
    void getTruckDetailsWithExceptionBecauseNoTruckInCompanyTest() throws Exception {
        Long truckId = 1L;
        Truck truck = initializeTestTruckWithDetailsById(truckId);
        truck.setId(truckId);

        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompanyAndTrucks.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName()))
                .thenReturn(userWithCompanyAndTrucks);
        when(truckServiceMock.findTruckWithDetailsById(anyLong())).thenReturn(truck);
        when(truckServiceMock.doesCompanyContainTruck(truck, userWithCompanyAndTrucks.getCompany()
                .getId())).thenReturn(false);

        MvcResult mvcResult = mockMvc.perform(get("/truck-details/{id}", 1L))
                .andExpect(status().isNotFound()).andReturn();

        Exception resolvedException = mvcResult.getResolvedException();
        Assertions.assertEquals("There is no truck with ID=1", resolvedException.getMessage());
        Assertions.assertTrue(resolvedException instanceof NoSuchTruckException);
    }

    @Test
    @WithMockUser(username = "admin")
    void checkTruckStatusTest() throws Exception {
        Long truckId = 1L;
        Truck truck = initializeTestTruckWithDetailsById(truckId);

        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompanyAndTrucks.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName()))
                .thenReturn(userWithCompanyAndTrucks);
        when(truckServiceMock.findTruckWithDetailsById(truckId)).thenReturn(truck);
        when(truckServiceMock.doesCompanyContainTruck(truck, userWithCompanyAndTrucks.getCompany()
                .getId())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(get("/check-truck-status/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("true", response);
    }

    @Test
    @WithMockUser(username = "admin")
    void checkTruckStatusWithAnotherStatusTest() throws Exception {
        Long truckId = 1L;
        Truck truck = initializeTestTruckWithDetailsById(truckId);
        truck.setStatus(TruckStatus.TRIP.getStatus);

        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompanyAndTrucks.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName()))
                .thenReturn(userWithCompanyAndTrucks);
        when(truckServiceMock.findTruckWithDetailsById(truckId)).thenReturn(truck);
        when(truckServiceMock.doesCompanyContainTruck(truck, userWithCompanyAndTrucks.getCompany()
                .getId())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(get("/check-truck-status/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("false", response);
        ArgumentCaptor<Long> truckIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(truckServiceMock, times(1)).findTruckWithDetailsById(truckIdCaptor.capture());
        Assertions.assertEquals(truckIdCaptor.getValue(), truckId);
    }

    @Test
    @WithMockUser(username = "admin")
    void sellTruckTest() throws Exception {
        Long truckId = 1L;
        Truck truck = initializeTestTruckWithDetailsById(truckId);

        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompanyAndTrucks.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName()))
                .thenReturn(userWithCompanyAndTrucks);
        when(truckServiceMock.findTruckWithDetailsById(truckId)).thenReturn(truck);
        when(truckServiceMock.doesCompanyContainTruck(truck, userWithCompanyAndTrucks.getCompany()
                .getId())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(post("/sell-truck/").param("truckId",
                String.valueOf(truckId)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("true", response);
        ArgumentCaptor<Truck> truckCaptor = ArgumentCaptor.forClass(Truck.class);
        verify(truckServiceMock, times(1)).sellTruck(truckCaptor.capture());
        Truck truckFromSellMethod = truckCaptor.getValue();
        Assertions.assertEquals(truck, truckFromSellMethod);
    }

    @Test
    @WithMockUser(username = "admin")
    void buyTruckTest() throws Exception {
        Integer newTruckId = 1;
        Company company = userWithCompanyAndTrucks.getCompany();
        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompanyAndTrucks.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName()))
                .thenReturn(userWithCompanyAndTrucks);

        MvcResult mvcResult = mockMvc.perform(post("/buy-truck/").param("truckId",
                String.valueOf(newTruckId)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("true", response);
        ArgumentCaptor<Integer> newTruckIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        verify(companyService, times(1)).addNewTruckToCompany(newTruckIdCaptor.capture(),
                companyCaptor.capture());
        Integer truckIdFromBuyMethod = newTruckIdCaptor.getValue();
        Company companyFromBuyMethod = companyCaptor.getValue();
        Assertions.assertEquals(newTruckId, truckIdFromBuyMethod);
        Assertions.assertEquals(company.getId(), companyFromBuyMethod.getId());
        Assertions.assertEquals(company.getBalance(), companyFromBuyMethod.getBalance());
    }

    @Test
    @WithMockUser(username = "admin")
    void serviceTruckTest() throws Exception {
        Long truckId = 1L;
        Truck truck = initializeTestTruckWithDetailsById(truckId);
        Company company = userWithCompanyAndTrucks.getCompany();
        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompanyAndTrucks.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName()))
                .thenReturn(userWithCompanyAndTrucks);
        when(truckServiceMock.findTruckWithDetailsById(truckId)).thenReturn(truck);
        when(truckServiceMock.doesCompanyContainTruck(truck, company.getId())).thenReturn(true);
        when(truckServiceMock.serviceTruck(truck)).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(post("/service-truck/").param("truckId",
                String.valueOf(truckId)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals(response, "true");
        ArgumentCaptor<Long> truckIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Truck> truckCaptor = ArgumentCaptor.forClass(Truck.class);
        verify(truckServiceMock, times(1)).findTruckWithDetailsById(truckIdCaptor.capture());
        verify(truckServiceMock, times(1)).serviceTruck(truckCaptor.capture());
        Assertions.assertEquals(truckId, truckIdCaptor.getValue());
        Truck truckFromServiceMethod = truckCaptor.getValue();
        Assertions.assertEquals(truck, truckFromServiceMethod);
    }
}