package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Company;

import java.text.ParseException;

public interface CompanyService {

    Company createNewCompany(String name);

    boolean isNameAlreadyExist(String name);

}
