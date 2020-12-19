package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.dto.ShipmentInfoDTO;
import com.myprojects.truckmanager.truckManagerApp.exception_handler.NoSuchTruckException;
import com.myprojects.truckmanager.truckManagerApp.exception_handler.ShipmentCreationException;
import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.Shipment;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.service.LocationService;
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
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ShipmentController {

    @Autowired
    private IAuthenticationFacade authenticationFacade;
    @Autowired
    private UserService userService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private TruckService truckService;
    @Autowired
    private ShipmentService shipmentService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringEditor);
    }

    @GetMapping("/create-shipment/{id}")
    public String showCreateShipmentForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        Truck truck = truckService.findTruckById(id);
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

    @PostMapping("/create-shipment")
    public String createNewShipment(@Valid @ModelAttribute("shipment") NewShipmentDTO newShipmentDTO,
                                    BindingResult result) {
        if (result.hasErrors()) {
            throw new ShipmentCreationException("An error occurred during shipment creation");
        }
        Shipment shipment = shipmentService.createNewShipment(newShipmentDTO);
        shipmentService.save(shipment);
        return "redirect:/homepage";
    }

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
        //model.addAttribute("time", LocalDateTime.now().plusMinutes(1));
        return "shipments";
    }

    private String getAuthenticationName() {
        return authenticationFacade.getAuthentication().getName();
    }
}
