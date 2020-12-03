package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Company;
import com.myprojects.truckmanager.truckManagerApp.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    CompanyRepository companyRepository;

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
