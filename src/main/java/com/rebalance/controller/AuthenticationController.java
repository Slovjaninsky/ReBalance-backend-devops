package com.rebalance.controller;

import com.rebalance.dto.request.LoginRequest;
import com.rebalance.dto.request.UserCreateRequest;
import com.rebalance.dto.response.UserResponse;
import com.rebalance.entity.User;
import com.rebalance.mapper.UserMapper;
import com.rebalance.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Validated UserCreateRequest user) {
        return new ResponseEntity<>(
                userMapper.userToResponse(
                        authenticationService.createUser(
                                userMapper.createRequestToUser(user), user.getCurrency())),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginWithEmailAndPassword(@RequestBody @Validated LoginRequest request) {
        Optional<User> user = authenticationService.authorizeUser(request.getEmail(), request.getPassword());

        return user.map(u -> ResponseEntity.ok(userMapper.userToResponse(u)))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
}
