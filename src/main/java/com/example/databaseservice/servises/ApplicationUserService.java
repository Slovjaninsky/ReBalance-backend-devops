package com.example.databaseservice.servises;

import com.example.databaseservice.entities.ApplicationUser;
import com.example.databaseservice.repositories.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public ApplicationUserService(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    public List<ApplicationUser> findAllUsers() {
        return applicationUserRepository.findAll();
    }

    public void saveApplicationUser(ApplicationUser user) {
        applicationUserRepository.save(user);
    }

    public Optional<ApplicationUser> getUserById(Long id) {
        return applicationUserRepository.findById(id);
    }

    public void deleteUserById(Long id) {
        applicationUserRepository.deleteById(id);
    }

    public ApplicationUser saveUser(ApplicationUser user){
        return applicationUserRepository.save(user);
    }

    public Optional<ApplicationUser> getUserByEmail(String email){
        return applicationUserRepository.findByEmail(email);
    }

}
