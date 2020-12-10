package com.myprojects.truckmanager.truckManagerApp.validation.shipment;

import com.myprojects.truckmanager.truckManagerApp.validation.authentication.UniqueCompanyNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CompanyExistsValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface CompanyExists {
    String message() default "You don't have a company with such id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default{};
}
