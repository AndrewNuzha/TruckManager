package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.dto.TruckDetailsDTO;
import com.myprojects.truckmanager.truckManagerApp.exception_handler.NoSuchTruckException;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.service.CompanyService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TruckController {

    @Autowired
    private IAuthenticationFacade authenticationFacade;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserService userService;
    @Autowired
    private TruckService truckService;

    @GetMapping("/trucks")
    public String showTrucksForm(Model model) {
        User user = userService.findUserWithCompanyByNickName(getAuthenticationName());
        Company company = user.getCompany();

        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", company.getBalance());
        model.addAttribute("trucks", company.getTrucks());
        return "trucks";
    }

    @GetMapping("/truckstore")
    public String showTruckStoreForm(Model model) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", user.getCompany().getBalance());
        model.addAttribute("trucks", truckService.getAllNewTrucks());
        return "truckstore";
    }

    @GetMapping("/truck-details/{id}")
    @ResponseBody
    public TruckDetailsDTO getTruckDetails(@PathVariable("id") Long id) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        Truck truck = truckService.findTruckWithDetailsById(id);
        if (truck == null) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else if (!truckService.doesCompanyContainTruck(truck, user.getCompany().getId())) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else {
            return truckService.prepareTruckDetails(truck);
        }
    }

    @GetMapping("/check-truck-status/{id}")
    @ResponseBody
    public boolean checkTruckStatus(@PathVariable("id") Long id) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        Truck truck = truckService.findTruckWithDetailsById(id);
        if (truck == null) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else if (!truckService.doesCompanyContainTruck(truck, user.getCompany().getId())) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else {
            return truck.getStatus().equals(TruckStatus.AVAILABLE.getStatus);
        }
    }

    @PostMapping("/sell-truck")
    @ResponseBody
    public boolean sellTruck(@RequestParam("truckId") Long id) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        Truck truck = truckService.findTruckWithDetailsById(id);
        if (truck == null) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else if (!truckService.doesCompanyContainTruck(truck, user.getCompany().getId())) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else {
            truckService.sellTruck(truck);
        }
        return true;
    }

    @PostMapping("/buy-truck")
    @ResponseBody
    public boolean buyTruck(@RequestParam("truckId") Integer id) {
        Company company = userService.findUserWithCompanyIdByNickName(getAuthenticationName()).getCompany();
        companyService.addNewTruckToCompany(id, company);

        return true;
    }

    @PostMapping("/service-truck")
    @ResponseBody
    public boolean serviceTruck(@RequestParam("truckId") Long id) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        Truck truck = truckService.findTruckWithDetailsById(id);
        if (truck == null) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else if (!truckService.doesCompanyContainTruck(truck, user.getCompany().getId())) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else {
            return truckService.serviceTruck(truck);
        }
    }

    private String getAuthenticationName() {
        return authenticationFacade.getAuthentication().getName();
    }
}
