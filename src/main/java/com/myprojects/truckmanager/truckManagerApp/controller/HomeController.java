package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import com.myprojects.truckmanager.truckManagerApp.exception_handler.NoSuchTruckException;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.service.TripService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

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
        User user = userService.findUserWithCompanyByNickName(getAuthenticationName());
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

    //TODO remove this method
    @GetMapping("/trips")
    public String showTripsForm(Model model) {
        User user = userService.findUserWithCompanyByNickName(getAuthenticationName());
        List<Trip> trips = tripService.prepareTripsForUser(user);

        Company company = user.getCompany();
        Truck one = company.getTrucks().get(0);
        Truck two = company.getTrucks().get(1);

        for (Trip trip : trips) {
            trip.addTruckToTrip(one);
            trip.addTruckToTrip(two);
        }

        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", user.getCompany().getBalance());
        model.addAttribute("trucks", user.getCompany().getTrucks());
        model.addAttribute("trips", trips);
        return "trips";
    }

    @GetMapping("/truck-details/{id}")
    public String showTruckDetailsForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findUserWithCompanyByNickName(getAuthenticationName());

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

    private String getAuthenticationName() {
        return authenticationFacade.getAuthentication().getName();
    }
}
