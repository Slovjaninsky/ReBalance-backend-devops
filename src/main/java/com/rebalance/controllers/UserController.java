package com.rebalance.controllers;

import com.rebalance.dto.LoginAndPassword;
import com.rebalance.dto.request.UserCreateRequest;
import com.rebalance.dto.response.GroupResponse;
import com.rebalance.dto.response.NotificationResponse;
import com.rebalance.dto.response.UserResponse;
import com.rebalance.entities.User;
import com.rebalance.mapper.GroupMapper;
import com.rebalance.mapper.NotificationMapper;
import com.rebalance.mapper.UserMapper;
import com.rebalance.servises.UserService;
import com.rebalance.servises.GroupService;
import com.rebalance.servises.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final GroupService groupService;
    private final NotificationService notificationService;
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final NotificationMapper notificationMapper;

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(
                userMapper.userToResponse(
                        userService.getUserByEmail(email)));
    }

    @GetMapping("/{id}/groups")
    public ResponseEntity<List<GroupResponse>> getAllGroupsByUserId(@PathVariable(value = "id") Long userId) {
        return ResponseEntity.ok(
                groupService.findAllGroupsByUserId(userId).stream()
                        .map(groupMapper::groupToResponse).collect(Collectors.toList()));
    }

    //TODO: move to auth controller
    @PostMapping()
    public ResponseEntity<UserResponse> register(@RequestBody UserCreateRequest user) {
        return new ResponseEntity<>(
                userMapper.userToResponse(userService.createUser(user)),
                HttpStatus.CREATED);
    }

    //TODO: move to auth controller
    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginWithEmailAndPassword(@RequestBody @Validated LoginAndPassword inputData) {
        Optional<User> user = userService.authorizeUser(inputData);

        return user.map(u -> ResponseEntity.ok(userMapper.userToResponse(u)))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
}
