package com.myprojects.truckmanager.truckManagerApp.validation.shipment;

import com.myprojects.truckmanager.truckManagerApp.service.CompanyService;
import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import com.myprojects.truckmanager.truckManagerApp.validation.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CompanyExistsValidator implements ConstraintValidator<CompanyExists, Long> {

    @Autowired
    CompanyService companyService;
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationFacade authentication;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        String name = authentication.getAuthentication().getName();
        Long userCompanyId = userService.findUserWithCompanyIdByNickName(name).getCompany().getId();
        return userCompanyId.equals(id);
    }
}
