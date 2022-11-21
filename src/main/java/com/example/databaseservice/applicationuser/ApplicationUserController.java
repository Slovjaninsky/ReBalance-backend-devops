package com.example.databaseservice.applicationuser;

import com.example.databaseservice.group.ExpenseGroup;
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
        Optional<ApplicationUser> user = applicationUserService.getUserById(id);

        if (user.isEmpty()) {
            return new ResponseEntity(null, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<ApplicationUser> getUserByEmail(@PathVariable("email") String email) {
        Optional<ApplicationUser> user = applicationUserService.getUserByEmail(email);

        if (user.isEmpty()) {
            return new ResponseEntity(null, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping("/users/{id}/groups")
    public ResponseEntity<List<ApplicationUser>> getAllGroupsByUserId(@PathVariable(value = "id") Long userId) {
        Optional<ApplicationUser> userOptional = applicationUserService.getUserById(userId);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        List<ExpenseGroup> groups = userOptional.get().getExpenseGroups().stream().collect(Collectors.toList());
        return new ResponseEntity(groups, HttpStatus.OK);
    }

    @GetMapping("/users/email/{email}/groups")
    public ResponseEntity<List<ApplicationUser>> getAllGroupsByUserEmail(@PathVariable(value = "email") String email) {
        Optional<ApplicationUser> userOptional = applicationUserService.getUserByEmail(email);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        List<ExpenseGroup> groups = userOptional.get().getExpenseGroups().stream().collect(Collectors.toList());
        return new ResponseEntity(groups, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<ApplicationUser> createUser(@RequestBody ApplicationUser inputUser) {
        ApplicationUser createdUser = new ApplicationUser(inputUser.getUsername(), inputUser.getEmail());
        if (inputUser.getId() != null) {
            createdUser.setId(inputUser.getId());
        }
        ApplicationUser user = applicationUserService.saveUser(createdUser);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApplicationUser> updateUser(@PathVariable("id") long id, @RequestBody ApplicationUser userInput) {
        Optional<ApplicationUser> userOptional = applicationUserService.getUserById(id);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ApplicationUser user = userOptional.get();

        user.setEmail(userInput.getEmail());
        user.setUsername(userInput.getUsername());

        return new ResponseEntity(applicationUserService.saveUser(user), HttpStatus.OK);
    }

    @PutMapping("/users/email/{email}")
    public ResponseEntity<ApplicationUser> updateUserByEmail(@PathVariable("email") String email, @RequestBody ApplicationUser userInput) {
        Optional<ApplicationUser> userOptional = applicationUserService.getUserByEmail(email);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ApplicationUser user = userOptional.get();

        if (user.getEmail() != null) {
            user.setEmail(userInput.getEmail());
        }
        if (user.getUsername() != null) {
            user.setUsername(userInput.getUsername());
        }
        return new ResponseEntity(applicationUserService.saveUser(user), HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        applicationUserService.deleteUserById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/users/email/{email}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("email") String email) {
        ApplicationUser user = applicationUserService.getUserByEmail(email).orElseThrow(() -> new RuntimeException("User with email " + email + " not found!"));
        applicationUserService.deleteUserById(user.getId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
