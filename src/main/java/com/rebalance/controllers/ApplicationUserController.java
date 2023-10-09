package com.rebalance.controllers;

import com.rebalance.dto.LoginAndPassword;
import com.rebalance.dto.UserWithPass;
import com.rebalance.entities.ApplicationUser;
import com.rebalance.entities.ExpenseGroup;
import com.rebalance.entities.Notification;
import com.rebalance.servises.ApplicationUserService;
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
@RequestMapping
@AllArgsConstructor
public class ApplicationUserController {
    private final ApplicationUserService applicationUserService;
    private final NotificationService notificationService;

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

    @PostMapping("/users")
    public ResponseEntity<LoginAndPassword> createUser(@RequestBody UserWithPass inputUser) {
        return new ResponseEntity<>(applicationUserService.createUser(inputUser), HttpStatus.CREATED);
    }

    @PostMapping("/users/login")
    public ResponseEntity<ApplicationUser> loginWithEmailAndPassword(@RequestBody LoginAndPassword inputData) {
        Optional<ApplicationUser> userOptional = applicationUserService.authorizeUser(inputData);
        if (userOptional.isPresent()) {
            return new ResponseEntity(userOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
