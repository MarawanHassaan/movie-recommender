package com.contenetwise.recommender.controllers;


import com.contenetwise.recommender.domain.User;
import com.contenetwise.recommender.dto.UserDTOResponse;
import com.contenetwise.recommender.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(summary = "Create a new user", description = "Add a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/create")
    public ResponseEntity<UserDTOResponse> createUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Check if username already exists
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new UserDTOResponse(existingUser.get().getId(), existingUser.get().getUsername()));
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(new UserDTOResponse(savedUser.getId(), savedUser.getUsername()));
    }

    @Operation(summary = "Get a list of all users", description = "Retrieve the list of users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    public ResponseEntity<List<UserDTOResponse>> getAllUsers() {
        List<UserDTOResponse> userDTOs = userRepository.findAll()
                .stream()
                .map(user -> new UserDTOResponse(user.getId(), user.getUsername()))
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
        Optional<User> user = userRepository.findById(id);

        return user.map(u -> ResponseEntity.ok(new UserDTOResponse(u.getId(), u.getUsername())))
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Modify a user by ID", description = "Update user by specifying the ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTOResponse> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(updatedUser.getUsername());
                    userRepository.save(existingUser);
                    return ResponseEntity.ok(new UserDTOResponse(existingUser.getId(), existingUser.getUsername()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a user by ID", description = "Remove user by specifying the ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        userRepository.deleteById(id);

        UserDTOResponse deletedUserDTO = new UserDTOResponse(user.getId(), user.getUsername());
        return ResponseEntity.ok("User deleted successfully.");
    }
}
