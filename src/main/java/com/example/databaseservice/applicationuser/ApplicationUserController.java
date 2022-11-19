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
        List<ApplicationUser> users = new ArrayList<ApplicationUser>();

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

    @GetMapping("/users/{id}/groups")
    public ResponseEntity<List<ApplicationUser>> getAllGroupsByUserId(@PathVariable(value = "id") Long userId) {
        Optional<ApplicationUser> userOptional = applicationUserService.getUserById(userId);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        List<ExpenseGroup> groups = userOptional.get().getExpenseGroups().stream().collect(Collectors.toList());
        return new ResponseEntity(groups, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<ApplicationUser> createUser(@RequestBody ApplicationUser inputUser) {
        ApplicationUser user = applicationUserService.saveUser(new ApplicationUser(
                        inputUser.getUsername(), inputUser.getEmail()
                )
        );
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

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        applicationUserService.deleteUserById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
