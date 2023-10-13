package com.rebalance.servises;

import com.rebalance.dto.LoginAndPassword;
import com.rebalance.dto.request.UserCreateRequest;
import com.rebalance.entities.User;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
    }

    public Optional<User> getUserOptionalById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUserById(Long id) {
        throwExceptionIfUserDoesNotExistById(id);
        userRepository.deleteById(id);
    }

    public void deleteUserByEmail(String email) {
        User user = getUserOptionalByEmail(email).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
        userRepository.deleteById(user.getId());
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
    }

    public Optional<User> getUserOptionalByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void throwExceptionIfUserExistsByEmail(String email) {
        if (getUserOptionalByEmail(email).isPresent()) {
            throw new RebalanceException(RebalanceErrorType.RB_001);
        }
    }

    public User createUser(UserCreateRequest inputUser) {
        throwExceptionIfUserExistsByEmail(inputUser.getEmail());
        User createdUser = new User();
        createdUser.setUsername(inputUser.getUsername());
        createdUser.setEmail(inputUser.getEmail());
        createdUser.setPassword(inputUser.getPassword());
        return userRepository.save(createdUser);
    }

    public User updateUser(Long id, User userInput) {
        User user = getUserById(id);
        throwExceptionIfUserExistsByEmail(userInput.getEmail());
        user.setEmail(userInput.getEmail());
        user.setUsername(userInput.getUsername());
        return userRepository.save(user);
    }

    public User updateUserByEmail(String email, User userInput) {
        User user = getUserByEmail(email);
        if (user.getEmail() != null) {
            user.setEmail(userInput.getEmail());
            throwExceptionIfUserExistsByEmail(userInput.getEmail());
        }
        if (user.getUsername() != null) {
            user.setUsername(userInput.getUsername());
        }
        return userRepository.save(user);
    }

    public void throwExceptionIfUserDoesNotExistById(Long id) {
        getUserById(id);
    }

    public Optional<User> authorizeUser(LoginAndPassword inputData) {
        User user = getUserByEmail(inputData.getEmail());
        if (user.getPassword().equals(inputData.getPassword())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

}
