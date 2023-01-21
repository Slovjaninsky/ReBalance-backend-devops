package com.example.databaseservice.controllers;

import com.example.databaseservice.entities.ApplicationUser;
import com.example.databaseservice.entities.ExpenseGroup;
import com.example.databaseservice.entities.Notification;
import com.example.databaseservice.exceptions.GroupNotFoundException;
import com.example.databaseservice.exceptions.UserNotFoundException;
import com.example.databaseservice.servises.ApplicationUserService;
import com.example.databaseservice.servises.GroupService;
import com.example.databaseservice.servises.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        List<ExpenseGroup> groups = new ArrayList<>();

        groupService.findAllGroups().forEach(groups::add);

        if (groups.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/groups/{id}/users")
    public ResponseEntity<List<ApplicationUser>> getAllUsersByGroupId(@PathVariable(value = "id") Long groupId) {
        ExpenseGroup group = groupService.getGroupById(groupId).orElseThrow(() -> new GroupNotFoundException("Not found Group with id = " + groupId));
        List<ApplicationUser> users = group.getUsers().stream().collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<ExpenseGroup> getGroupById(@PathVariable(value = "id") Long id) {
        ExpenseGroup group = groupService.getGroupById(id).orElseThrow(() -> new GroupNotFoundException("Not found Group with id = " + id));
        return new ResponseEntity(group, HttpStatus.OK);
    }

    @PutMapping("/groups/{id}")
    public ResponseEntity<ExpenseGroup> updateGroup(@PathVariable("id") long id, @RequestBody ExpenseGroup inputGroup) {
        ExpenseGroup group = groupService.getGroupById(id).orElseThrow(() -> new GroupNotFoundException("Not found Group with id = " + id));
        if (inputGroup.getName() != null) {
            group.setName(inputGroup.getName());
        }
        if (inputGroup.getCurrency() != null) {
            group.setCurrency(inputGroup.getCurrency());
        }
        return new ResponseEntity(groupService.saveGroup(group), HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/groups")
    public ResponseEntity<ExpenseGroup> addGroup(@PathVariable(value = "userId") Long userId, @RequestBody ExpenseGroup inputGroup) {
        ExpenseGroup group = applicationUserService.getUserById(userId).map(user -> {
                    Long groupId = inputGroup.getId();
                    if (groupId != null && groupId != -1) {
                        ExpenseGroup newGroup = groupService.getGroupById(groupId).orElseThrow(() -> new GroupNotFoundException("Not found Group with id = " + groupId));
                        user.addGroup(newGroup);
                        applicationUserService.saveUser(user);
                        return newGroup;
                    }
                    user.addGroup(inputGroup);
                    return groupService.saveGroup(inputGroup);
                }
        ).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + userId));
        notificationService.saveNotification(new Notification(userId, userId, group.getId(), -1D, false));
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @PostMapping("/users/email/{email}/groups")
    public ResponseEntity<ExpenseGroup> addGroup(@PathVariable(value = "email") String email, @RequestBody ExpenseGroup inputGroup) {
        ExpenseGroup group = applicationUserService.getUserByEmail(email).map(user -> {
                    Long groupId = inputGroup.getId();
                    if (groupId != null) {
                        ExpenseGroup newGroup = groupService.getGroupById(groupId).orElseThrow(() -> new GroupNotFoundException("Not found Group with id = " + groupId));
                        user.addGroup(newGroup);
                        applicationUserService.saveUser(user);
                        return newGroup;
                    }
                    user.addGroup(inputGroup);
                    return groupService.saveGroup(inputGroup);
                }
        ).orElseThrow(() -> new UserNotFoundException("Not found User with email = " + email));
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @DeleteMapping("/groups/{groupId}/users/{userId}")
    public ResponseEntity<HttpStatus> deleteUserFromGroup(@PathVariable(value = "userId") Long userId, @PathVariable(value = "groupId") Long groupId) {
        ApplicationUser applicationUser = applicationUserService.getUserById(userId).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + userId));
        applicationUser.removeGroup(groupId);
        applicationUserService.saveUser(applicationUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/groups/{id}")
    public ResponseEntity<HttpStatus> deleteGroup(@PathVariable("id") long id) {
        groupService.getGroupById(id).orElseThrow(() -> new GroupNotFoundException("Not found Group with id = " + id));
        groupService.deleteGroupById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
