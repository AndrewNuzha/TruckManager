package com.myprojects.truckmanager.truckManagerApp.controller;

import com.myprojects.truckmanager.truckManagerApp.dto.UserRegistrationDTO;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Handles registration requests.
 *
 * @author Andrew Nuzha
 * @version 1.0
 */
@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    @Autowired
    private UserService userService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringEditor);
    }

    /**
     * Shows registration form.
     *
     * @param model container with info for form
     * @return registration form
     */
    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "registration";
    }

    /**
     * Initializes the process of registering a new user.
     * if the object {@code userRegistrationDTO} contains errors, redirection to the registration form will be happened.
     *
     * @param userRegistrationDTO an object with new user data for registration
     * @param result              result of validation of the new user data
     * @return registration form or registration form with success param
     */
    @PostMapping
    public String registerUserAccount(@Valid @ModelAttribute("user") UserRegistrationDTO userRegistrationDTO,
                                      BindingResult result) {
        if (result.hasErrors()) {
            return "registration";
        } else {
            userService.save(userRegistrationDTO);
            return "redirect:/registration?success";
        }
    }
}
