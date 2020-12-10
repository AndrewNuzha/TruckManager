package com.myprojects.truckmanager.truckManagerApp.validation.authentication;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueCompanyNameValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UniqueCompanyName {
    String message() default "There is already a company with this name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default{};
}
