package com.rebalance.controller;

import com.rebalance.dto.request.GroupAddUserRequest;
import com.rebalance.dto.request.GroupCreateRequest;
import com.rebalance.dto.request.GroupFavoriteRequest;
import com.rebalance.dto.response.GroupResponse;
import com.rebalance.dto.response.GroupUserResponse;
import com.rebalance.dto.response.UserGroupResponse;
import com.rebalance.mapper.GroupMapper;
import com.rebalance.mapper.UserMapper;
import com.rebalance.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Group management")
@RestController
@RequestMapping(APIVersion.current + "/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;

    @Operation(summary = "Get all users of group")
    @GetMapping("/{id}/users")
    public ResponseEntity<List<GroupUserResponse>> getAllUsersByGroupId(@PathVariable(value = "id") Long groupId) {
        return ResponseEntity.ok(
                groupService.getAllUsersOfGroup(groupId).stream()
                        .map(userMapper::userToGroupResponse).collect(Collectors.toList()));
    }

    @Operation(summary = "Get group information")
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(
                groupMapper.groupToResponse(
                        groupService.getGroupInfoById(id)));
    }

    @Operation(summary = "Create new group")
    @PostMapping()
    public ResponseEntity<GroupResponse> createGroupAndAddUser(@RequestBody @Validated GroupCreateRequest request) {
        return new ResponseEntity<>(
                groupMapper.groupToResponse(
                        groupService.createGroupAndAddUser(
                                groupMapper.createRequestToGroup(request))),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Add user to existing group")
    @PostMapping("/users")
    public ResponseEntity<UserGroupResponse> addUserToGroup(@RequestBody @Validated GroupAddUserRequest request) {
        return new ResponseEntity<>(
                userMapper.userGroupToResponse(
                        groupService.addUserToGroup(request.getGroupId(), request.getEmail())),
                HttpStatus.OK
        );
    }

    @Operation(summary = "Make group favorite ot not")
    @PostMapping("/set-favorite")
    public ResponseEntity<String> setFavorite(@RequestBody @Validated GroupFavoriteRequest request) {
        groupService.setFavorite(request.getGroupId(), request.getFavorite());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
