package com.myprojects.truckmanager.truckManagerApp.validation.authentication;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueNicknameValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UniqueNickname {
    String message() default "There is already user with this nickname";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default{};
}
