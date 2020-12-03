package com.myprojects.truckmanager.truckManagerApp.authentication;

import com.myprojects.truckmanager.truckManagerApp.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueCompanyNameValidator implements ConstraintValidator<UniqueCompanyName, String> {

    @Autowired
    CompanyService companyService;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        return name != null && !companyService.isNameAlreadyExist(name);
    }
}
