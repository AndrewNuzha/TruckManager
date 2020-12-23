package com.myprojects.truckmanager.truckManagerApp.service.impl;

import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.model.NewTruck;
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

        NewTruck newTruckModel = truckService.getAllNewTrucks().get(0);
        Company newCompany = new Company(name, 20000F);
        Truck firstStarterTruck = truckService.createTruck(newTruckModel);
        firstStarterTruck.setCompany(newCompany);
        truckService.saveTruck(firstStarterTruck);
        Truck secondStarterTruck = truckService.createTruck(newTruckModel);
        secondStarterTruck.setCompany(newCompany);
        truckService.saveTruck(secondStarterTruck);
        return newCompany;
    }

    @Transactional
    @Override
    public void addNewTruckToCompany(Integer newTruckId, Company company) {
        NewTruck newTruckModel = truckService.getAllNewTrucks().get(newTruckId - 1);
        Truck newTruck = truckService.createTruck(newTruckModel);
        company.setBalance(company.getBalance() - newTruckModel.getPrice());
        newTruck.setCompany(company);
        truckService.saveTruck(newTruck);
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
