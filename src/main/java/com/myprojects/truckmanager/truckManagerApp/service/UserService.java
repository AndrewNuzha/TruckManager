package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.dto.UserRegistrationDTO;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User save(UserRegistrationDTO userRegistrationDTO);

    User findUserWithCompanyByNickName(String nickName);

    User findUserWithCompanyIdByNickName(String nickName);

    boolean isNickNameAlreadyExist(String nickName);

}
