package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Company;

public interface CompanyService {

    Company createNewCompany(String name);

    boolean isNameAlreadyExist(String name);

    Company findCompanyById(Long id);

}
