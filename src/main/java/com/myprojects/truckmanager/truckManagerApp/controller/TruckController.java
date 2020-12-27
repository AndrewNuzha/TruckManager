package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.dto.TruckDetailsDTO;
import com.myprojects.truckmanager.truckManagerApp.exception_handler.NoSuchTruckException;
import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import com.myprojects.truckmanager.truckManagerApp.model.TruckStatus;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.service.CompanyService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Handles trucks requests.
 *
 * @author Andrew Nuzha
 * @version 1.0
 */
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

    /**
     * Returns a form with a list of all the trucks in the company.
     *
     * @param model container with info for form
     * @return form with a list of all trucks
     */
    @GetMapping("/trucks")
    public String showTrucksForm(Model model) {
        User user = userService.findUserWithCompanyByNickName(getAuthenticationName());
        Company company = user.getCompany();

        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", company.getBalance());
        model.addAttribute("trucks", company.getTrucks());
        return "trucks";
    }

    /**
     * Returns a form with a list of new trucks available for purchase.
     *
     * @param model container with info for form
     * @return form with a list of new trucks
     */
    @GetMapping("/truckstore")
    public String showTruckStoreForm(Model model) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", user.getCompany().getBalance());
        model.addAttribute("trucks", truckService.getAllNewTrucks());
        return "truckstore";
    }

    /**
     * Returns a form with details for selected truck
     *
     * @param id identifier of truck
     * @return form with truck details
     */
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

    /**
     * Checks status of selected truck.
     *
     * @param id identifier of truck
     * @return boolean result of status check
     * @throws NoSuchTruckException if there is no truck with selected id {@code id}.
     *                              The truck status is checked before truck service and truck sale.
     *                              The status should be "available".
     */
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

    /**
     * Initiates the truck sale process.
     * At this stage, we are confident that the truck has correct status.
     *
     * @param id identifier of truck
     * @return boolean result of selling
     * @throws NoSuchTruckException if there is no truck with selected {@code id}.
     */
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

    /**
     * Initializes the truck buying process.
     * At this stage, we are confident that the company has sufficient balance.
     *
     * @param id identifier of new truck
     * @return boolean result of buying
     */
    @PostMapping("/buy-truck")
    @ResponseBody
    public boolean buyTruck(@RequestParam("truckId") Integer id) {
        Company company = userService.findUserWithCompanyIdByNickName(getAuthenticationName()).getCompany();
        companyService.addNewTruckToCompany(id, company);

        return true;
    }

    /**
     * Initializes the truck service process.
     * At this stage, we are confident that the truck has correct status.
     *
     * @param id identifier of truck
     * @return boolean result of truck service
     * @throws NoSuchTruckException if there is no truck with selected {@code id}.
     */
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

    /**
     * Returns the nickname of user, who is currently authenticated
     *
     * @return nickname of authenticated user
     */
    private String getAuthenticationName() {
        return authenticationFacade.getAuthentication().getName();
    }
}
