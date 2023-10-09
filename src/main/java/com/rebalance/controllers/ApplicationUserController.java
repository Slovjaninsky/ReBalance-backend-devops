package com.rebalance.controllers;

import com.rebalance.dto.LoginAndPassword;
import com.rebalance.dto.UserWithPass;
import com.rebalance.entities.Notification;
import com.rebalance.exceptions.InvalidRequestException;
import com.rebalance.servises.ApplicationUserService;
import com.rebalance.entities.ApplicationUser;
import com.rebalance.entities.ExpenseGroup;
import com.rebalance.servises.NotificationService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;
    private final NotificationService notificationService;

    @GetMapping("/users")
    public ResponseEntity<List<ApplicationUser>> getAllUsers() {
        List<ApplicationUser> users = new ArrayList<>();
        applicationUserService.findAllUsers().forEach(users::add);
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApplicationUser> getUserById(@PathVariable("id") long id) {
        ApplicationUser user = applicationUserService.getUserById(id);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<ApplicationUser> getUserByEmail(@PathVariable("email") String email) {
        ApplicationUser user = applicationUserService.getUserByEmail(email);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping("/users/{id}/groups")
    public ResponseEntity<List<ApplicationUser>> getAllGroupsByUserId(@PathVariable(value = "id") Long userId) {
        ApplicationUser user = applicationUserService.getUserById(userId);
        List<ExpenseGroup> groups = user.getExpenseGroups().stream().collect(Collectors.toList());
        return new ResponseEntity(groups, HttpStatus.OK);
    }

    @GetMapping("/users/{id}/notifications")
    public ResponseEntity<List<ApplicationUser>> getAllNotificationsByUserId(@PathVariable(value = "id") Long userId) {
        List<Notification> notifications = notificationService.findAllByUserIdAndDeleteThem(userId);
        return new ResponseEntity(notifications, HttpStatus.OK);
    }

    @GetMapping("/users/email/{email}/groups")
    public ResponseEntity<List<ApplicationUser>> getAllGroupsByUserEmail(@PathVariable(value = "email") String email) {
        ApplicationUser user = applicationUserService.getUserByEmail(email);
        List<ExpenseGroup> groups = user.getExpenseGroups().stream().collect(Collectors.toList());
        return new ResponseEntity(groups, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<LoginAndPassword> createUser(@RequestBody UserWithPass inputUser) {
        if (inputUser.getEmail() == null || inputUser.getUsername() == null || inputUser.getPassword() == null) {
            throw new InvalidRequestException("Request body should contain email, password and username fields");
        }
        return new ResponseEntity<>(applicationUserService.createUser(inputUser), HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApplicationUser> updateUser(@PathVariable("id") long id, @RequestBody ApplicationUser userInput) {
        if (userInput.getEmail() == null || userInput.getUsername() == null) {
            throw new InvalidRequestException("Request body should contain email and username fields");
        }
        return new ResponseEntity(applicationUserService.updateUser(id, userInput), HttpStatus.OK);
    }

    @PutMapping("/users/email/{email}")
    public ResponseEntity<ApplicationUser> updateUserByEmail(@PathVariable("email") String email, @RequestBody ApplicationUser userInput) {
        return new ResponseEntity(applicationUserService.updateUserByEmail(email, userInput), HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        applicationUserService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/users/email/{email}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("email") String email) {
        applicationUserService.deleteUserByEmail(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/users/login")
    public ResponseEntity<ApplicationUser> loginWithEmailAndPassword(@RequestBody LoginAndPassword inputData) {
        if (inputData.getEmail() == null || inputData.getPassword() == null) {
            throw new InvalidRequestException("Request body should contain login and password");
        }
        Optional<ApplicationUser> userOptional = applicationUserService.authorizeUser(inputData);
        if (userOptional.isPresent()) {
            return new ResponseEntity(userOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
