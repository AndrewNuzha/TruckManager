package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.authentication.IAuthenticationFacade;
import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/homepage")
    public String showHomePage(Model model) {
        String nickName = authenticationFacade.getAuthentication().getName();
        User user = userService.findUserWithCompanyByNickName(nickName);
        Company company = user.getCompany();
        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", company.getBalance());
        return "homepage";
    }

    @GetMapping("/trucks")
    public String showTrucksPage(Model model) {
        String nickName = authenticationFacade.getAuthentication().getName();
        User user = userService.findUserWithCompanyByNickName(nickName);
        Company company = user.getCompany();

        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", company.getBalance());
        model.addAttribute("trucks", company.getTrucks());
        return "trucks";
    }
}
