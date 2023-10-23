package com.rebalance.controllers;

import com.rebalance.dto.response.GroupResponse;
import com.rebalance.dto.response.UserResponse;
import com.rebalance.mapper.GroupMapper;
import com.rebalance.mapper.UserMapper;
import com.rebalance.servises.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
