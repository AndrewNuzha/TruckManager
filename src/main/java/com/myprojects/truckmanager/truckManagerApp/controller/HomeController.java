package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.authentication.IAuthenticationFacade;
import com.myprojects.truckmanager.truckManagerApp.exception_handler.NoSuchTruckException;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.service.TripService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private IAuthenticationFacade authenticationFacade;
    @Autowired
    private UserService userService;
    @Autowired
    private TripService tripService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/homepage")
    public String showHomeForm(Model model) {
        String nickName = authenticationFacade.getAuthentication().getName();
        User user = userService.findUserWithCompanyByNickName(nickName);
        Company company = user.getCompany();
        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", company.getBalance());
        return "homepage";
    }

    @GetMapping("/trucks")
    public String showTrucksForm(Model model) {
        String nickName = authenticationFacade.getAuthentication().getName();
        User user = userService.findUserWithCompanyByNickName(nickName);
        Company company = user.getCompany();

        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", company.getBalance());
        model.addAttribute("trucks", company.getTrucks());
        return "trucks";
    }

    @GetMapping("/trips")
    public String showTripsForm(Model model) {
        String nickName = authenticationFacade.getAuthentication().getName();
        User user = userService.findUserWithCompanyByNickName(nickName);
        List<Trip> trips = tripService.prepareTripsForUser(user);

        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", user.getCompany().getBalance());
        model.addAttribute("trucks", user.getCompany().getTrucks());
        model.addAttribute("trips", trips);
        return "trips";
    }

    @GetMapping("/truck-details/{id}")
    public String showTruckDetailsForm(@PathVariable("id") Long id, Model model) {
        String nickName = authenticationFacade.getAuthentication().getName();
        User user = userService.findUserWithCompanyByNickName(nickName);

        Truck truck = user.getCompany().getTrucks().stream().filter(tr -> tr.getId().equals(id))
                .findAny().orElse(null);
        if (truck == null) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else {
            TruckDetails details = truck.getDetails();
            model.addAttribute("details", details);
            model.addAttribute("username", user.getNickName());
            model.addAttribute("balance", user.getCompany().getBalance());
            model.addAttribute("trucks", user.getCompany().getTrucks());
            return "truck-details";
        }
    }
}
