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
    User findByNickName(String nickName);

    /**
     * Returns user object from DB with company
     * @param nickName nickname
     * @return user from DB
     */
    @EntityGraph(attributePaths = {"company", "company.trucks"})
    User findWithCompanyByNickName(String nickName);

}
