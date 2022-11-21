package com.example.databaseservice.group;

import com.example.databaseservice.applicationuser.ApplicationUser;
import com.example.databaseservice.applicationuser.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class GroupController {

    private final GroupService groupService;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public GroupController(GroupService groupService, ApplicationUserService applicationUserService) {
        this.groupService = groupService;
        this.applicationUserService = applicationUserService;
    }

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
        Optional<ExpenseGroup> groupOptional = groupService.getGroupById(groupId);
        if (groupOptional.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        List<ApplicationUser> users = groupOptional.get().getUsers().stream().collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<ExpenseGroup> getGroupById(@PathVariable(value = "id") Long id) {
        Optional<ExpenseGroup> groupOptional = groupService.getGroupById(id);

        if (groupOptional.isEmpty()) {
            return new ResponseEntity(null, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(groupOptional.get(), HttpStatus.OK);
    }

    @PutMapping("/groups/{id}")
    public ResponseEntity<ExpenseGroup> updateGroup(@PathVariable("id") long id, @RequestBody ExpenseGroup inputGroup) {
        Optional<ExpenseGroup> groupOptional = groupService.getGroupById(id);

        if (groupOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ExpenseGroup group = groupOptional.get();

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

            if (groupId != null) {
                ExpenseGroup newGroup = groupService.getGroupById(groupId)
                        .orElseThrow(() -> new RuntimeException("Not found Group with id = " + groupId));
                user.addGroup(newGroup);
                applicationUserService.saveUser(user);
                return newGroup;
            }
            user.addGroup(inputGroup);
            return groupService.saveGroup(inputGroup);
        }).orElseThrow(() -> new RuntimeException("Not found User with id = " + userId));

        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @PostMapping("/users/{email}/groups")
    public ResponseEntity<ExpenseGroup> addGroup(@PathVariable(value = "email") String email, @RequestBody ExpenseGroup inputGroup) {
        ExpenseGroup group = applicationUserService.getUserByEmail(email).map(user -> {
            Long groupId = inputGroup.getId();

            if (groupId != null) {
                ExpenseGroup newGroup = groupService.getGroupById(groupId)
                        .orElseThrow(() -> new RuntimeException("Not found Group with id = " + groupId));
                user.addGroup(newGroup);
                applicationUserService.saveUser(user);
                return newGroup;
            }
            user.addGroup(inputGroup);
            return groupService.saveGroup(inputGroup);
        }).orElseThrow(() -> new RuntimeException("Not found User with email = " + email));

        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @DeleteMapping("/groups/{groupId}/users/{userId}")
    public ResponseEntity<HttpStatus> deleteUserFromGroup(@PathVariable(value = "userId") Long userId, @PathVariable(value = "groupId") Long groupId) {
        ApplicationUser applicationUser = applicationUserService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User with id = " + userId));

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
