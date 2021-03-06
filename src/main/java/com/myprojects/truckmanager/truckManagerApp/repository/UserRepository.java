package com.myprojects.truckmanager.truckManagerApp.repository;

import com.myprojects.truckmanager.truckManagerApp.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Returns user object from DB with roles
     * @param nickName nickname
     * @return user from DB
     */
    @EntityGraph(attributePaths = {"roles"})
    User findWithRolesByNickName(String nickName);

    /**
     * Returns user object from DB with company, trucks and trucks details
     * @param nickName nickname
     * @return user from DB
     */
    @EntityGraph(attributePaths = {"company", "company.trucks.details"})
    User findWithCompanyAndTrucksInfoByNickName(String nickName);

    @EntityGraph(attributePaths = {"company"})
    User findWithCompanyByNickName(String nickName);

    @EntityGraph(attributePaths = {"company", "logs"})
    User findWithCompanyAndLogsByNickName(String nickName);

}
