package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.Log;
import com.myprojects.truckmanager.truckManagerApp.model.NewTruck;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.service.LogService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;
    @MockBean
    private TruckService truckServiceMock;
    @MockBean
    private LogService logServiceMock;
    @MockBean
    private IAuthenticationFacade authenticationFacadeMock;

    private final Authentication authentication = mock(Authentication.class);
    private Company company;
    private User userWithCompany;

    @BeforeEach
    public void initializeUserAndCompany() {
        company = new Company();
        company.setName("ShipmentCompany");
        company.setBalance(9999.99f);
        userWithCompany = new User();
        userWithCompany.setNickName("Nickname");
        userWithCompany.setCompany(company);
    }

    @Test
    @WithMockUser(username = "admin")
    void showHomeFormTest() throws Exception {
        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompany.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName())).thenReturn(userWithCompany);

        mockMvc.perform(get("/homepage"))
                .andExpect(status().isOk())
                .andExpect(view().name("homepage"))
                .andExpect(model().attribute("balance", is(userWithCompany.getCompany().getBalance())))
                .andExpect(model().attribute("username", is(userWithCompany.getNickName())));

        verify(userServiceMock, times(1))
                .findUserWithCompanyIdByNickName(userWithCompany.getNickName());
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    @WithMockUser(username = "admin")
    void checkBalanceNotEnoughTest() throws Exception {
        company.setBalance(999.99f);
        List<NewTruck> newTruckList = new ArrayList<>();
        newTruckList.add(new NewTruck(1, "Scania RX100", 10, 0.52f, 15000));
        newTruckList.add(new NewTruck(2, "MAN 818", 20, 0.48f, 20000));

        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompany.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName())).thenReturn(userWithCompany);
        when(truckServiceMock.getAllNewTrucks()).thenReturn(newTruckList);

        MvcResult mvcResult = mockMvc.perform(get("/check-balance/{id}", 1))
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("false", response);
        verify(userServiceMock, times(1))
                .findUserWithCompanyIdByNickName(userWithCompany.getNickName());
        verify(truckServiceMock, times(1))
                .getAllNewTrucks();
        verifyNoMoreInteractions(userServiceMock);
        verifyNoMoreInteractions(truckServiceMock);
    }

    @Test
    @WithMockUser(username = "admin")
    void checkBalanceEnoughTest() throws Exception {
        company.setBalance(19999.99f);
        List<NewTruck> newTruckList = new ArrayList<>();
        newTruckList.add(new NewTruck(1, "Scania RX100", 10, 0.52f, 15000));
        newTruckList.add(new NewTruck(2, "MAN 818", 20, 0.48f, 20000));

        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompany.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName())).thenReturn(userWithCompany);
        when(truckServiceMock.getAllNewTrucks()).thenReturn(newTruckList);

        MvcResult mvcResult = mockMvc.perform(get("/check-balance/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("true", response);
    }

    @Test
    @WithMockUser(username = "admin")
    void showLogsFormWithoutPagesTest() throws Exception {
        Page<Log> logPage = mock(Page.class);
        int page = 1;
        when(authenticationFacadeMock.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithCompany.getNickName());
        when(userServiceMock.findUserWithCompanyIdByNickName(authentication.getName())).thenReturn(userWithCompany);
        when(logServiceMock.getLogRecordsCount(userWithCompany.getId())).thenReturn(3L);
        when(logServiceMock.getLogsOnPageByUserId(userWithCompany.getId(), page, 3))
                .thenReturn(logPage);

        mockMvc.perform(get("/logs/").param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("balance", is(userWithCompany.getCompany().getBalance())))
                .andExpect(model().attribute("username", is(userWithCompany.getNickName())))
                .andExpect(model().attribute("showPages", is(false)))
                .andExpect(model().attribute("currentPage", is(page)));
    }
}