package com.rebalance.controllers;

import com.rebalance.entities.ApplicationUser;
import com.rebalance.entities.ExpenseGroup;
import com.rebalance.entities.Notification;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.servises.ApplicationUserService;
import com.rebalance.servises.GroupService;
import com.rebalance.servises.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@AllArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final NotificationService notificationService;
    private final ApplicationUserService applicationUserService;

    @GetMapping("/groups")
    public ResponseEntity<List<ExpenseGroup>> getAllGroups() {
        List<ExpenseGroup> groups = groupService.findAllGroups();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/groups/{id}/users")
    public ResponseEntity<List<ApplicationUser>> getAllUsersByGroupId(@PathVariable(value = "id") Long groupId) {
        ExpenseGroup group = groupService.getGroupById(groupId);
        List<ApplicationUser> users = group.getUsers().stream().collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<ExpenseGroup> getGroupById(@PathVariable(value = "id") Long id) {
        ExpenseGroup group = groupService.getGroupById(id);
        return new ResponseEntity(group, HttpStatus.OK);
    }

    @PutMapping("/groups/{id}")
    public ResponseEntity<ExpenseGroup> updateGroup(@PathVariable("id") Long id, @RequestBody ExpenseGroup inputGroup) {
        return new ResponseEntity(groupService.updateGroup(id, inputGroup), HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/groups")
    public ResponseEntity<ExpenseGroup> addGroup(@PathVariable(value = "userId") Long userId, @RequestBody ExpenseGroup inputGroup) {
        ExpenseGroup group = applicationUserService.getUserOptionalById(userId).map(user -> {
                    Long groupId = inputGroup.getId();
                    if (groupId != null && groupId != -1) {
                        ExpenseGroup newGroup = groupService.getGroupById(groupId);
                        user.addGroup(newGroup);
                        applicationUserService.saveUser(user);
                        return newGroup;
                    }
                    user.addGroup(inputGroup);
                    return groupService.saveGroup(inputGroup);
                }
        ).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
        notificationService.saveNotification(new Notification(userId, userId, group.getId(), -1D, false));
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @PostMapping("/users/email/{email}/groups")
    public ResponseEntity<ExpenseGroup> addGroup(@PathVariable(value = "email") String email, @RequestBody ExpenseGroup inputGroup) {
        ExpenseGroup group = applicationUserService.getUserOptionalByEmail(email).map(user -> {
                    Long groupId = inputGroup.getId();
                    if (groupId != null) {
                        ExpenseGroup newGroup = groupService.getGroupById(groupId);
                        user.addGroup(newGroup);
                        applicationUserService.saveUser(user);
                        return newGroup;
                    }
                    user.addGroup(inputGroup);
                    return groupService.saveGroup(inputGroup);
                }
        ).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @DeleteMapping("/groups/{groupId}/users/{userId}")
    public ResponseEntity<HttpStatus> deleteUserFromGroup(@PathVariable(value = "userId") Long userId, @PathVariable(value = "groupId") Long groupId) {
        ApplicationUser applicationUser = applicationUserService.getUserById(userId);
        applicationUser.removeGroup(groupId);
        applicationUserService.saveUser(applicationUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/groups/{id}")
    public ResponseEntity<HttpStatus> deleteGroup(@PathVariable("id") long id) {
        groupService.deleteGroupById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
