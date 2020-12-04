package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.dto.UserRegistrationDTO;
import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.Role;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User save(UserRegistrationDTO userRegistrationDTO) {

        Company newCompany = companyService.createNewCompany(userRegistrationDTO.getCompanyName());
        User newUser = new User(userRegistrationDTO.getFirstName(),
                userRegistrationDTO.getLastName(),
                userRegistrationDTO.getNickName(),
                userRegistrationDTO.getEmail(),
                passwordEncoder.encode(userRegistrationDTO.getPassword()),
                Arrays.asList(new Role("USER")));

        newUser.addCompanyToUser(newCompany);

        return userRepository.save(newUser);
    }

    @Override
    public User findUserWithCompanyByNickName(String nickName) {
        return userRepository.findWithCompanyByNickName(nickName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNickNameAlreadyExist(String nickName) {
        User userFromDb = userRepository.findByNickName(nickName);
        if (userFromDb == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        log.info("Getting user by nickname: {}", userName);
        User userFromDb = userRepository.findByNickName(userName);
        if (userFromDb == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        return new org.springframework.security.core.userdetails.User(userFromDb.getNickName(),
                userFromDb.getPassword(), mapRolesToAuthorities(userFromDb.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
