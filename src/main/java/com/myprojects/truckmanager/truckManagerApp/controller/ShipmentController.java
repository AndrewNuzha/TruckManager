package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.exception_handler.ShipmentCreationException;
import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import com.myprojects.truckmanager.truckManagerApp.dto.NewShipmentDTO;
import com.myprojects.truckmanager.truckManagerApp.exception_handler.NoSuchTruckException;
import com.myprojects.truckmanager.truckManagerApp.model.Location;
import com.myprojects.truckmanager.truckManagerApp.model.Shipment;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.service.LocationService;
import com.myprojects.truckmanager.truckManagerApp.service.ShipmentService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    private ShipmentService shipmentService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringEditor);
    }

    @GetMapping("/create-shipment/{id}")
    public String showCreateShipmentForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findUserWithCompanyByNickName(getAuthenticationName());

        Truck truck = user.getCompany().getTrucks().stream().filter(tr -> tr.getId().equals(id))
                .findAny().orElse(null);
        if (truck == null) {
            throw new NoSuchTruckException("There is no truck with ID=" + id);
        } else {
            List<Location> availableLocations = locationService.getLocationsForTruck(truck);
            model.addAttribute("disabled", "true");
            model.addAttribute("username", user.getNickName());
            model.addAttribute("balance", user.getCompany().getBalance());
            model.addAttribute("locations", availableLocations);
            NewShipmentDTO newShipmentDTO = initializeShipmentDTO(user, truck, availableLocations);

            model.addAttribute("shipment", newShipmentDTO);
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

    private String getAuthenticationName() {
        return authenticationFacade.getAuthentication().getName();
    }

    private NewShipmentDTO initializeShipmentDTO(User user, Truck truck, List<Location> availableLocations) {
        NewShipmentDTO newShipmentDTO = new NewShipmentDTO();
        newShipmentDTO.setTruckId(truck.getId());
        newShipmentDTO.setCompanyId(user.getCompany().getId());
        newShipmentDTO.setDepartureLocationId(truck.getDetails().getCurrentLocation().getId());
        if (availableLocations.size() > 0) {
            newShipmentDTO.setArrivalLocationId(availableLocations.get(0).getId());
        }
        newShipmentDTO.setCategory("simple");
        return newShipmentDTO;
    }
}
