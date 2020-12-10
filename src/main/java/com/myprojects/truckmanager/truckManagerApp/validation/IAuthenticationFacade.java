package com.myprojects.truckmanager.truckManagerApp.validation;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
}
