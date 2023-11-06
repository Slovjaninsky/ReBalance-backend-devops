package com.rebalance.controller;

import com.rebalance.dto.request.GroupAddUserRequest;
import com.rebalance.dto.request.GroupCreateRequest;
import com.rebalance.dto.response.GroupResponse;
import com.rebalance.dto.response.GroupUserResponse;
import com.rebalance.dto.response.UserGroupResponse;
import com.rebalance.mapper.GroupMapper;
import com.rebalance.mapper.UserMapper;
import com.rebalance.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(APIVersion.current + "/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;

    @GetMapping("/{id}/users")
    public ResponseEntity<List<GroupUserResponse>> getAllUsersByGroupId(@PathVariable(value = "id") Long groupId) {
        return ResponseEntity.ok(
                groupService.getAllUsersOfGroup(groupId).stream()
                        .map(userMapper::userToGroupResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(
                groupMapper.groupToResponse(
                        groupService.getNotPersonalGroupById(id)));
    }

    @PostMapping()
    public ResponseEntity<GroupResponse> createGroupAndAddUser(@RequestBody @Validated GroupCreateRequest request) {
        return new ResponseEntity<>(
                groupMapper.groupToResponse(
                        groupService.createGroupAndAddUser(
                                groupMapper.createRequestToGroup(request))),
                HttpStatus.CREATED);
    }

    @PostMapping("/users")
    public ResponseEntity<UserGroupResponse> addUserToGroup(@RequestBody @Validated GroupAddUserRequest request) {
        return new ResponseEntity<>(
                userMapper.userGroupToResponse(
                        groupService.addUserToGroup(request.getGroupId(), request.getEmail())),
                HttpStatus.OK
        );
    }
}
