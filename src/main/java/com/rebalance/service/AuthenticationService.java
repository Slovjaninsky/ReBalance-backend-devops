package com.rebalance.service;

import com.rebalance.entity.Group;
import com.rebalance.entity.User;
import com.rebalance.entity.UserRole;
import com.rebalance.security.CognitoClient;
import com.rebalance.security.JwtService;
import lombok.RequiredArgsConstructor;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final GroupService groupService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    // private final AuthenticationProvider authenticationProvider;
    private final JwtService jwtService;
    private final CognitoClient cognitoClient;

    public User createUser(User userRequest, String currency) {
        userService.validateUserNotExists(userRequest.getEmail());

        // Create the user in Cognito
        String cognitoUserId = cognitoClient.signUpUser(
            userRequest.getEmail(),
            userRequest.getPassword(),
            userRequest.getEmail()
        );
        userRequest.setCognitoID(cognitoUserId);

        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRequest.setRole(UserRole.USER);

        User user = userService.save(userRequest);

        Group personalGroup = new Group();
        personalGroup.setName("personal_" + user.getEmail());
        personalGroup.setCurrency(currency);
        personalGroup.setCreator(user);
        personalGroup.setPersonal(true);
        Group group = groupService.createGroupAndAddUser(personalGroup, user);

        user.setCreatedGroups(Set.of(group));

        return user;
    }

    public String authorizeUser(String email, String password) {
        User user = userService.getUserByEmailOnLogin(email);

        // authenticationProvider.authenticate(
        //     new UsernamePasswordAuthenticationToken(email, password)
        // );

        // Authenticate the user in Cognito and retrieve a token
        String cognitoToken = cognitoClient.authenticate(email, password);
        jwtService.saveToken(user.getId(), cognitoToken);

        return cognitoToken;
    }

    public void logout() {
        // Clear the token from local storage
        String jwt = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        jwtService.deleteToken(jwt);
    }
}