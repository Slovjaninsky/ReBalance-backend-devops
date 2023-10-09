package com.rebalance.repositories;

import com.rebalance.entities.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    public Optional<ApplicationUser> findByEmail(String email);

}
