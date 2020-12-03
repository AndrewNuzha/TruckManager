package com.myprojects.truckmanager.truckManagerApp.authentication;

import com.myprojects.truckmanager.truckManagerApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueNicknameValidator implements ConstraintValidator<UniqueNickname, String> {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String nickName, ConstraintValidatorContext constraintValidatorContext) {
        return nickName != null && !userService.isNickNameAlreadyExist(nickName);
    }
}
