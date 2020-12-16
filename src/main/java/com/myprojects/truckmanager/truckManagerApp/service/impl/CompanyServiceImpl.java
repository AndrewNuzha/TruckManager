package com.myprojects.truckmanager.truckManagerApp.service.impl;

import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import com.myprojects.truckmanager.truckManagerApp.repository.CompanyRepository;
import com.myprojects.truckmanager.truckManagerApp.service.CompanyService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    TruckService truckService;


    /**
     * Initializes a new company with default balance and trucks
     *
     * @param name company name
     * @return created company
     */
    @Override
    public Company createNewCompany(String name) {

        Company newCompany = new Company(name, 20000L);
        Truck newTruck = truckService.createStarterTruck();
        newTruck.setCompany(newCompany);
        truckService.saveTruck(newTruck);
        Truck secondTruck = truckService.createStarterTruck(); //testing only
        secondTruck.setCompany(newCompany);
        truckService.saveTruck(secondTruck);
        return newCompany;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNameAlreadyExist(String name) {
        Company companyFromDb = companyRepository.findByName(name);
        if (companyFromDb == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Company findCompanyById(Long id) {
        return companyRepository.findCompanyById(id);
    }

    @Override
    @Transactional
    public void saveCompany(Company company) {
        companyRepository.save(company);
    }
}
