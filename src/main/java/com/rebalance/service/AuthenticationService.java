package com.rebalance.service;

import com.rebalance.entity.Group;
import com.rebalance.entity.User;
import com.rebalance.entity.UserRole;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.UserRepository;
import com.rebalance.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationProvider authenticationProvider;
    private final JwtService jwtService;

    public User createUser(User userRequest, String currency) {
        validateUserNotExists(userRequest.getEmail());

        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRequest.setRole(UserRole.USER);
        User user = userRepository.save(userRequest);

        Group personalGroup = new Group();
        personalGroup.setName("personal_" + user.getEmail());
        personalGroup.setCurrency(currency);
        personalGroup.setCreator(user);
        personalGroup.setPersonal(true);
        Group group = groupService.createGroupAndAddUser(personalGroup);

        user.setCreatedGroups(Set.of(group));

        return user;
    }

    public String authorizeUser(String email, String password) {
        User user = userService.getUserByEmail(email);

        authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String token = jwtService.generateToken(user);
        jwtService.saveToken(user.getId(), token);

        return token;
    }

    private void validateUserNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RebalanceException(RebalanceErrorType.RB_001);
        }
    }
}
