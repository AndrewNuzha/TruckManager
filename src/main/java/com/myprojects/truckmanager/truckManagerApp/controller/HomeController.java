package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.Log;
import com.myprojects.truckmanager.truckManagerApp.model.NewTruck;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.service.LogService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles homepage and company requests
 *
 * @author Andrew Nuzha
 * @version 1.0
 */
@Controller
public class HomeController {

    @Autowired
    private IAuthenticationFacade authenticationFacade;
    @Autowired
    private UserService userService;
    @Autowired
    private TruckService truckService;
    @Autowired
    private LogService logService;
    private final int RECORD_ON_PAGE = 4;

    /**
     * Shows login form to enter login data.
     *
     * @return login form
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    /**
     * Shows the main page of the company
     *
     * @param model container with company info
     * @return homepage
     */
    @GetMapping("/homepage")
    public String showHomeForm(Model model) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        Company company = user.getCompany();
        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", company.getBalance());
        return "homepage";
    }

    /**
     * Checks balance of company before truck purchase
     *
     * @param id The identifier of the truck that user wants to buy
     * @return result of checking if there is enough balance
     */
    @GetMapping("/check-balance/{id}")
    @ResponseBody
    public boolean checkBalance(@PathVariable("id") Integer id) {
        Float balance = userService.findUserWithCompanyIdByNickName(getAuthenticationName()).getCompany().getBalance();
        NewTruck newTruck = truckService.getAllNewTrucks().get(id - 1);
        if (newTruck == null) {
            return false;
        } else {
            return balance > newTruck.getPrice();
        }
    }

    /**
     * Shows paged company logs by request
     *
     * @param model container with company info
     * @param page  number of page, user wants to see
     * @return logs page
     */
    @GetMapping("/logs")
    public String showLogsForm(Model model, @RequestParam int page) {
        User user = userService.findUserWithCompanyIdByNickName(getAuthenticationName());
        long recordsCount = logService.getLogRecordsCount(user.getId());
        boolean showPages = recordsCount > RECORD_ON_PAGE;
        long totalPages = recordsCount / RECORD_ON_PAGE;
        if (recordsCount % RECORD_ON_PAGE > 0) {
            totalPages++;
        }
        model.addAttribute("username", user.getNickName());
        model.addAttribute("balance", user.getCompany().getBalance());
        model.addAttribute("data", logService.getLogsOnPageByUserId(user.getId(),
                page - 1, RECORD_ON_PAGE)); //TODO implement time formatting and price round
        model.addAttribute("showPages", showPages);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        return "logs";
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
