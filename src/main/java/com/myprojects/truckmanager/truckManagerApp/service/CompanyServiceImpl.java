package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import com.myprojects.truckmanager.truckManagerApp.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

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
        Truck firstTruck = new Truck();
        firstTruck.setModel("Scania RX100");
        firstTruck.setFuelConsumption(0.41f);
        firstTruck.setMileage(0L);
        firstTruck.setMaxLoad(25);
        firstTruck.setProductionYear(new Timestamp(new Date().getTime()));

        firstTruck.setCompany(newCompany);
        truckService.saveTruck(firstTruck);

        return newCompany;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isNameAlreadyExist(String name) {
        Company companyFromDb = companyRepository.findByName(name);
        if (companyFromDb == null) {
            return false;
        } else {
            return true;
        }
    }
}
