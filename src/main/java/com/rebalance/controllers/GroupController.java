package com.rebalance.controllers;

import com.rebalance.dto.request.GroupAddUserRequest;
import com.rebalance.dto.request.GroupCreateRequest;
import com.rebalance.dto.response.GroupResponse;
import com.rebalance.dto.response.UserResponse;
import com.rebalance.mapper.GroupMapper;
import com.rebalance.mapper.UserMapper;
import com.rebalance.servises.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;

    @GetMapping("/{id}/users")
    public ResponseEntity<List<UserResponse>> getAllUsersByGroupId(@PathVariable(value = "id") Long groupId) {
        return ResponseEntity.ok(
                groupService.getAllUsersOfGroup(groupId).stream()
                        .map(userMapper::userToResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(
                groupMapper.groupToResponse(
                        groupService.getGroupById(id)));
    }

    @PostMapping()
    public ResponseEntity<String> createGroup(@RequestBody GroupCreateRequest request) {
        groupService.createGroup(groupMapper.createRequestToGroup(request));

        //TODO: add notifications
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/users")
    public ResponseEntity<String> addUserToGroup(@RequestBody GroupAddUserRequest request) {
        groupService.addUserToGroup(request.getGroupId(), request.getEmail());

        //TODO: add notifications
        return ResponseEntity.ok().build();
    }
}
