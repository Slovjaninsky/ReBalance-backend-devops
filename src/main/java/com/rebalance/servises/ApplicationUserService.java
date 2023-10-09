package com.rebalance.servises;

import com.rebalance.dto.LoginAndPassword;
import com.rebalance.dto.request.UserCreateRequest;
import com.rebalance.entities.ApplicationUser;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
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
        return applicationUserRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
    }

    public Optional<ApplicationUser> getUserOptionalById(Long id) {
        return applicationUserRepository.findById(id);
    }

    public void deleteUserById(Long id) {
        throwExceptionIfUserDoesNotExistById(id);
        applicationUserRepository.deleteById(id);
    }

    public void deleteUserByEmail(String email) {
        ApplicationUser user = getUserOptionalByEmail(email).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
        applicationUserRepository.deleteById(user.getId());
    }

    public ApplicationUser saveUser(ApplicationUser user) {
        return applicationUserRepository.save(user);
    }

    public ApplicationUser getUserByEmail(String email) {
        return applicationUserRepository.findByEmail(email).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
    }

    public Optional<ApplicationUser> getUserOptionalByEmail(String email) {
        return applicationUserRepository.findByEmail(email);
    }

    public void throwExceptionIfUserExistsByEmail(String email) {
        if (getUserOptionalByEmail(email).isPresent()) {
            throw new RebalanceException(RebalanceErrorType.RB_001);
        }
    }

    public ApplicationUser createUser(UserCreateRequest inputUser) {
        throwExceptionIfUserExistsByEmail(inputUser.getEmail());
        ApplicationUser createdUser = new ApplicationUser(inputUser.getUsername(), inputUser.getEmail());
        createdUser.setPassword(inputUser.getPassword());
        return applicationUserRepository.save(createdUser);
    }

    public ApplicationUser updateUser(Long id, ApplicationUser userInput) {
        ApplicationUser user = getUserById(id);
        throwExceptionIfUserExistsByEmail(userInput.getEmail());
        user.setEmail(userInput.getEmail());
        user.setUsername(userInput.getUsername());
        return applicationUserRepository.save(user);
    }

    public ApplicationUser updateUserByEmail(String email, ApplicationUser userInput) {
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

    public void throwExceptionIfUserDoesNotExistById(Long id) {
        getUserById(id);
    }

    public Optional<ApplicationUser> authorizeUser(LoginAndPassword inputData) {
        ApplicationUser user = getUserByEmail(inputData.getEmail());
        if (user.getPassword().equals(inputData.getPassword())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

}
