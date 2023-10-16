package com.rebalance.controllers;

import com.rebalance.dto.LoginAndPassword;
import com.rebalance.dto.request.UserCreateRequest;
import com.rebalance.dto.response.GroupResponse;
import com.rebalance.dto.response.UserResponse;
import com.rebalance.entities.User;
import com.rebalance.mapper.GroupMapper;
import com.rebalance.mapper.UserMapper;
import com.rebalance.servises.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(
                userMapper.userToResponse(
                        userService.getUserByEmail(email)));
    }

    @GetMapping("/{id}/groups")
    public ResponseEntity<List<GroupResponse>> getAllGroupsByUserId(@PathVariable(value = "id") Long userId) {
        return ResponseEntity.ok(
                userService.getAllGroupsOfUser(userId).stream()
                        .map(groupMapper::groupToResponse).toList());
    }

    @PostMapping()
    public ResponseEntity<UserResponse> register(@RequestBody UserCreateRequest user) {
        return new ResponseEntity<>(
                userMapper.userToResponse(
                        userService.createUser(
                                userMapper.createRequestToUser(user))),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginWithEmailAndPassword(@RequestBody @Validated LoginAndPassword request) {
        Optional<User> user = userService.authorizeUser(request.getEmail(), request.getPassword());

        return user.map(u -> ResponseEntity.ok(userMapper.userToResponse(u)))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
}
