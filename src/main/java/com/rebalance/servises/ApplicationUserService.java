package com.rebalance.servises;

import com.rebalance.dto.LoginAndPassword;
import com.rebalance.dto.UserWithPass;
import com.rebalance.entities.ApplicationUser;
import com.rebalance.exceptions.EmailTakenException;
import com.rebalance.exceptions.UserNotFoundException;
import com.rebalance.repositories.ApplicationUserRepository;
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

    public ApplicationUser getUserById(Long id) {
        return applicationUserRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + id));
    }

    public Optional<ApplicationUser> getUserOptionalById(Long id) {
        return applicationUserRepository.findById(id);
    }

    public void deleteUserById(Long id) {
        throwExceptionIfUserDoesNotExistById(id);
        applicationUserRepository.deleteById(id);
    }

    public void deleteUserByEmail(String email) {
        ApplicationUser user = getUserOptionalByEmail(email).orElseThrow(() -> new UserNotFoundException("Not found User with email = " + email));
        applicationUserRepository.deleteById(user.getId());
    }

    public ApplicationUser saveUser(ApplicationUser user){
        return applicationUserRepository.save(user);
    }

    public ApplicationUser getUserByEmail(String email){
        return applicationUserRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Not found User with email = " + email));
    }

    public Optional<ApplicationUser> getUserOptionalByEmail(String email){
        return applicationUserRepository.findByEmail(email);
    }

    public void throwExceptionIfUserExistsByEmail(String email){
        if (getUserOptionalByEmail(email).isPresent()) {
            throw new EmailTakenException("Email " + email + " is already taken!");
        }
    }

    public LoginAndPassword createUser(UserWithPass inputUser){
        throwExceptionIfUserExistsByEmail(inputUser.getEmail());
        ApplicationUser createdUser = new ApplicationUser(inputUser.getUsername(), inputUser.getEmail());
        createdUser.setPassword(inputUser.getPassword());
        ApplicationUser user = applicationUserRepository.save(createdUser);
        return new LoginAndPassword(user.getEmail(), user.getPassword());
    }

    public ApplicationUser updateUser(Long id, ApplicationUser userInput){
        ApplicationUser user = getUserById(id);
        throwExceptionIfUserExistsByEmail(userInput.getEmail());
        user.setEmail(userInput.getEmail());
        user.setUsername(userInput.getUsername());
        return applicationUserRepository.save(user);
    }

    public ApplicationUser updateUserByEmail(String email, ApplicationUser userInput){
        ApplicationUser user = getUserByEmail(email);
        if (user.getEmail() != null) {
            user.setEmail(userInput.getEmail());
            throwExceptionIfUserExistsByEmail(userInput.getEmail());
        }
        if (user.getUsername() != null) {
            user.setUsername(userInput.getUsername());
        }
        return applicationUserRepository.save(user);
    }

    public void throwExceptionIfUserDoesNotExistById(Long id){
        getUserById(id);
    }

    public Optional<ApplicationUser> authorizeUser(LoginAndPassword inputData){
        ApplicationUser user = getUserByEmail(inputData.getEmail());
        if (user.getPassword().equals(inputData.getPassword())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

}
