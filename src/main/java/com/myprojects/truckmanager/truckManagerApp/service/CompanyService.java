package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.NewTruck;

public interface CompanyService {

    Company createNewCompany(String name);

    boolean isNameAlreadyExist(String name);

    Company findCompanyById(Long id);

    void addNewTruckToCompany(Integer newTruckId, Company company);

    void saveCompany(Company company);

}
