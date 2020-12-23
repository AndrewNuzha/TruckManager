package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.NewTruck;
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

    @GetMapping("/check-balance/{id}")
    @ResponseBody
    public boolean checkTruckStatus(@PathVariable("id") Integer id) {
        Float balance = userService.findUserWithCompanyIdByNickName(getAuthenticationName()).getCompany().getBalance();
        NewTruck newTruck = truckService.getAllNewTrucks().get(id - 1);
        if (newTruck == null) {
            return false;
        } else {
            return balance > newTruck.getPrice();
        }
    }


    private String getAuthenticationName() {
        return authenticationFacade.getAuthentication().getName();
    }
}
