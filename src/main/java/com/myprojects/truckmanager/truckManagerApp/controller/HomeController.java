package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.exception_handler.NoSuchTruckException;
import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import com.myprojects.truckmanager.truckManagerApp.model.TruckDetails;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @Autowired
    private IAuthenticationFacade authenticationFacade;
    @Autowired
    private UserService userService;
    @Autowired
    private TruckService truckService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/homepage")
    public String showHomeForm(Model model) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        Company company = user.getCompany();
        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", company.getBalance());
        return "homepage";
    }

    @GetMapping("/trucks")
    public String showTrucksForm(Model model) {
        User user = userService.findUserWithCompanyByNickName(getAuthenticationName());
        Company company = user.getCompany();

        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", company.getBalance());
        model.addAttribute("trucks", company.getTrucks());
        return "trucks";
    }

    @GetMapping("/truck-details/{id}")
    @ResponseBody
    public TruckDetails showTruckDetailsForm(@PathVariable("id") Long id) {
        Truck truck = truckService.findTruckWithDetailsById(id);
        if (truck == null) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else {
            TruckDetails details = truck.getDetails();
            return truck.getDetails();
        }
    }

    private String getAuthenticationName() {
        return authenticationFacade.getAuthentication().getName();
    }
}
