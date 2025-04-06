package com.contenetwise.recommender.controllers;


import com.contenetwise.recommender.domain.User;
import com.contenetwise.recommender.dto.UserDTOResponse;
import com.contenetwise.recommender.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Operations related to user")
public class UserController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(summary = "Create a new user", description = "Add a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of user"),
            @ApiResponse(responseCode = "400", description = "Invalid genre name")
    })
    @PostMapping("/create")
    public ResponseEntity<UserDTOResponse> createUser(@RequestBody UserDTOResponse user) {
        logger.info("Create request received for user with username: {}", user.getUsername());
        //Check if username is correct and not empty
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            logger.warn("Invalid genre name provided: '{}'", user.getUsername());
            return ResponseEntity.badRequest().build();
        }
        //Return the user
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            logger.warn("User with username '{}' already exists. Creation aborted.", user.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new UserDTOResponse(existingUser.get().getUsername())); // Return existing user
        }

        // Convert UserDTOResponse to User entity and save
        User userToSave = new User();
        userToSave.setUsername(user.getUsername());
        User savedUser = userRepository.save(userToSave);

        logger.info("User created successfully with username: {}", savedUser.getUsername());
        return ResponseEntity.ok(new UserDTOResponse(savedUser.getUsername()));
    }

    @Operation(summary = "Get a list of all users", description = "Retrieve the list of users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of users"),
    })
    @GetMapping
    public ResponseEntity<List<UserDTOResponse>> getAllUsers() {
        logger.info("Get request called for all users");
        //Return all users
        List<UserDTOResponse> userDTOs = userRepository.findAll()
                .stream()
                .map(user -> new UserDTOResponse(user.getUsername()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }

    @Operation(summary = "Get a user by ID", description = "Retrieve user by specifying the ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> getUserById(@PathVariable Long id) {
        logger.info("Request called for user with ID: {}", id);
        //Check if user exists or not
        return userRepository.findById(id)
                .map(user -> {
                    logger.info("User found: {} with ID: {}", user.getUsername(), id);
                    return ResponseEntity.ok(new UserDTOResponse(user.getUsername()));
                })
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }


    @Operation(summary = "Modify a user by ID", description = "Update user by specifying the ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTOResponse> updateUser(@PathVariable Long id, @RequestBody UserDTOResponse updatedUser) {
        logger.info("Update request received for user with ID: {}", id);
        //Check if user exists or not
        return userRepository.findById(id)
                .map(existingUser -> {
                    logger.info("User found with ID: {}. Updating username from '{}' to '{}'",
                            id, existingUser.getUsername(), updatedUser.getUsername());

                    existingUser.setUsername(updatedUser.getUsername());
                    // If needed, update additional fields here, like password, email, etc.

                    User savedUser = userRepository.save(existingUser);

                    logger.info("User updated successfully with ID: {}", id);
                    return ResponseEntity.ok(new UserDTOResponse(savedUser.getUsername()));
                })
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found. Update failed.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Delete a user by ID", description = "Remove user by specifying the ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        logger.info("Delete request received for user with ID: {}", id);
        //Check if user exists or not
        if (!userRepository.existsById(id)) {
            logger.warn("User with ID {} not found. Deletion aborted.", id);
            return ResponseEntity.notFound().build();
        }
        //Delete the user
        userRepository.deleteById(id);
        logger.info("User with ID {} deleted successfully.", id);
        return ResponseEntity.ok("User deleted successfully.");
    }
}
