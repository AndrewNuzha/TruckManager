package com.myprojects.truckmanager.truckManagerApp.repository;

import com.myprojects.truckmanager.truckManagerApp.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Company findByName(String name);

    Company findCompanyById(Long id);

    @Modifying
    @Query("update Company company set company.balance = :balance where company.id = :id")
    void updateBalance(@Param("balance") Float balance, @Param("id") Long id);
}
