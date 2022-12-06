package com.example.databaseservice.controllers;

import com.example.databaseservice.entities.LoginAndPassword;
import com.example.databaseservice.exceptions.EmailTakenException;
import com.example.databaseservice.exceptions.InvalidRequestException;
import com.example.databaseservice.exceptions.UserNotFoundException;
import com.example.databaseservice.servises.ApplicationUserService;
import com.example.databaseservice.entities.ApplicationUser;
import com.example.databaseservice.entities.ExpenseGroup;
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
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationUserController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

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
        ApplicationUser user = applicationUserService.getUserById(id).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + id));
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<ApplicationUser> getUserByEmail(@PathVariable("email") String email) {
        ApplicationUser user = applicationUserService.getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("Not found User with email = " + email));
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping("/users/{id}/groups")
    public ResponseEntity<List<ApplicationUser>> getAllGroupsByUserId(@PathVariable(value = "id") Long userId) {
        ApplicationUser user = applicationUserService.getUserById(userId).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + userId));
        List<ExpenseGroup> groups = user.getExpenseGroups().stream().collect(Collectors.toList());
        return new ResponseEntity(groups, HttpStatus.OK);
    }

    @GetMapping("/users/email/{email}/groups")
    public ResponseEntity<List<ApplicationUser>> getAllGroupsByUserEmail(@PathVariable(value = "email") String email) {
        ApplicationUser user = applicationUserService.getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("Not found User with email = " + email));
        List<ExpenseGroup> groups = user.getExpenseGroups().stream().collect(Collectors.toList());
        return new ResponseEntity(groups, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<LoginAndPassword> createUser(@RequestBody ApplicationUser inputUser) {
        if (inputUser.getEmail() == null || inputUser.getUsername() == null) {
            throw new InvalidRequestException("Request body should contain email and username fields");
        }
        if (applicationUserService.getUserByEmail(inputUser.getEmail()).isPresent()) {
            throw new EmailTakenException("Email " + inputUser.getEmail() + " is already taken!");
        }
        ApplicationUser createdUser = new ApplicationUser(inputUser.getUsername(), inputUser.getEmail());
        Random rand = new Random();
        createdUser.setPassword(rand.ints(rand.nextInt(25), 48, 91).mapToObj(i -> String.valueOf((char)i)).collect(Collectors.joining()));
        ApplicationUser user = applicationUserService.saveUser(createdUser);
        return new ResponseEntity<>(new LoginAndPassword(user.getEmail(), user.getPassword()), HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApplicationUser> updateUser(@PathVariable("id") long id, @RequestBody ApplicationUser userInput) {
        if (userInput.getEmail() == null || userInput.getUsername() == null) {
            throw new InvalidRequestException("Request body should contain email and username fields");
        }
        ApplicationUser user = applicationUserService.getUserById(id).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + id));
        if (applicationUserService.getUserByEmail(userInput.getEmail()).isPresent()) {
            throw new EmailTakenException("Email " + userInput.getEmail() + " is already taken!");
        }
        user.setEmail(userInput.getEmail());
        user.setUsername(userInput.getUsername());
        return new ResponseEntity(applicationUserService.saveUser(user), HttpStatus.OK);
    }

    @PutMapping("/users/email/{email}")
    public ResponseEntity<ApplicationUser> updateUserByEmail(@PathVariable("email") String email, @RequestBody ApplicationUser userInput) {
        ApplicationUser user = applicationUserService.getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("Not found User with email = " + email));
        if (user.getEmail() != null) {
            user.setEmail(userInput.getEmail());
            if (applicationUserService.getUserByEmail(userInput.getEmail()).isPresent()) {
                throw new EmailTakenException("Email " + userInput.getEmail() + " is already taken!");
            }
        }
        if (user.getUsername() != null) {
            user.setUsername(userInput.getUsername());
        }
        return new ResponseEntity(applicationUserService.saveUser(user), HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        applicationUserService.getUserById(id).orElseThrow(() -> new UserNotFoundException("Not found User with id = " + id));
        applicationUserService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/users/email/{email}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("email") String email) {
        ApplicationUser user = applicationUserService.getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("Not found User with email = " + email));
        applicationUserService.deleteUserById(user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/users/login")
    public ResponseEntity<ApplicationUser> loginWithEmailAndPassword(@RequestBody LoginAndPassword inputData) {
        if (inputData.getEmail() == null || inputData.getPassword() == null) {
            throw new InvalidRequestException("Request body should contain login and password");
        }
        ApplicationUser user = applicationUserService.getUserByEmail(inputData.getEmail()).orElseThrow(() -> new UserNotFoundException("Not found User with email = " + inputData.getEmail()));
        if (user.getPassword().equals(inputData.getPassword())) {
            return new ResponseEntity(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
