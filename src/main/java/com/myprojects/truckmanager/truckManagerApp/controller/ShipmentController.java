package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.dto.ShipmentInfoDTO;
import com.myprojects.truckmanager.truckManagerApp.exception_handler.NoSuchTruckException;
import com.myprojects.truckmanager.truckManagerApp.exception_handler.ShipmentCreationException;
import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.Shipment;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.service.ShipmentService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Handles shipment requests.
 *
 * @author Andrew Nuzha
 * @version 1.0
 */
@Controller
public class ShipmentController {

    @Autowired
    private IAuthenticationFacade authenticationFacade;
    @Autowired
    private UserService userService;
    @Autowired
    private TruckService truckService;
    @Autowired
    private ShipmentService shipmentService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringEditor);
    }

    /**
     * Shows form for shipment creation for selected truck with id {@code id}.
     *
     * @param id    identifier of truck
     * @param model container with info for form
     * @return form for shipment creation
     */
    @GetMapping("/create-shipment/{id}")
    public String showCreateShipmentForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        Truck truck = truckService.findTruckWithDetailsById(id);
        if (truck == null) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else {
            model.addAttribute("username", user.getNickName());
            model.addAttribute("balance", user.getCompany().getBalance());
            List<NewShipmentDTO> availableShipmentOrders = shipmentService.getAvailableShipmentOrders(truck);
            if (availableShipmentOrders.size() > 0) {
                model.addAttribute("shipments", availableShipmentOrders);
            } else {
                model.addAttribute("shipments", null);
            }
            return "create-shipment";
        }
    }

    /**
     * Initializes the shipment creation process.
     * On this stage we have all the parameters of the shipment, we have to create.
     *
     * @param newShipmentDTO object with shipment parameters
     * @param result         result of checking shipment parameters
     * @return redirection to homepage
     * @throws ShipmentCreationException if there are errors in {@code newShipmentDTO} object.
     */
    @PostMapping("/create-shipment")
    public String createNewShipment(@Valid @ModelAttribute("shipment") NewShipmentDTO newShipmentDTO,
                                    BindingResult result) {
        if (result.hasErrors()) {
            throw new ShipmentCreationException("An error occurred during shipment creation");
        }
        Shipment shipment = shipmentService.createNewShipment(newShipmentDTO);
        shipmentService.saveShipment(shipment);
        return "redirect:/homepage";
    }

    /**
     * Returns a form with company shipments list.
     * To update the status and complete shipments, user must open this form
     *
     * @param model container with info for form
     * @return form with company shipments list
     */
    @GetMapping("/shipments")
    public String showShipmentsForm(Model model) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        Company company = user.getCompany();
        List<Shipment> companyShipments = shipmentService.findShipmentsByCompanyId(company.getId());
        List<Shipment> actualShipments = shipmentService.getActualShipments(companyShipments);
        List<ShipmentInfoDTO> companyShipmentsInfo = shipmentService.prepareShipmentsData(actualShipments);

        model.addAttribute("shipments", companyShipmentsInfo);
        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", company.getBalance());
        return "shipments";
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
