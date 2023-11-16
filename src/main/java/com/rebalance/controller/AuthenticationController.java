package com.rebalance.controller;

import com.rebalance.dto.request.LoginRequest;
import com.rebalance.dto.request.UserCreateRequest;
import com.rebalance.dto.response.LoginResponse;
import com.rebalance.dto.response.UserWithTokenResponse;
import com.rebalance.entity.User;
import com.rebalance.mapper.UserMapper;
import com.rebalance.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management")
@AllArgsConstructor
@RestController
@RequestMapping(APIVersion.current + "/user")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ResponseEntity<UserWithTokenResponse> register(@RequestBody @Validated UserCreateRequest request) {
        User user = authenticationService.createUser(
                userMapper.createRequestToUser(request), request.getCurrency());
        String token = authenticationService.authorizeUser(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(
                userMapper.userToResponseWithToken(user, token),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Login user (get new JWT)")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginWithEmailAndPassword(@RequestBody @Validated LoginRequest request) {
        return new ResponseEntity<>(
                userMapper.tokenToResponse(
                        authenticationService.authorizeUser(request.getEmail(), request.getPassword())
                ),
                HttpStatus.OK);
    }

    @Operation(summary = "Logout user (invalidate JWT)")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        authenticationService.logout();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
