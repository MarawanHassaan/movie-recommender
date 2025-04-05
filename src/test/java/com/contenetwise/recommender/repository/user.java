package com.contenetwise.recommender.repository;

import com.contenetwise.recommender.domain.User;
import com.contenetwise.recommender.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Automatically rolls back the transaction after each test
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // Prepare a user before each test
        user = User.builder()
                .username("john_doe")
                .build();
        userRepository.save(user); // Save the user for testing
    }

    @Test
    void testFindByUsernameShouldReturnUserWhenExists() {
        // Find the user by username
        Optional<User> foundUser = userRepository.findByUsername("john_doe");

        // Assert that the user is present and matches the expected username
        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals("john_doe", foundUser.get().getUsername(), "Username should match");
    }

    @Test
    void testFindByUsernameShouldReturnEmptyWhenNotExists() {
        // Try to find a user that does not exist
        Optional<User> foundUser = userRepository.findByUsername("non_existent_user");

        // Assert that no user is found
        assertFalse(foundUser.isPresent(), "User should not be found");
    }

    @Test
    void testSaveUser() {
        // Create a new user
        User newUser = User.builder()
                .username("jane_doe")
                .build();

        // Save the new user
        User savedUser = userRepository.save(newUser);

        // Assert that the saved user has a non-null ID and the correct username
        assertNotNull(savedUser.getId(), "Saved user should have a generated ID");
        assertEquals("jane_doe", savedUser.getUsername(), "Username should match");
    }

    @Test
    void testDeleteUser() {
        // Delete the user created in the @BeforeEach setup
        userRepository.delete(user);

        // Try to find the deleted user
        Optional<User> deletedUser = userRepository.findByUsername("john_doe");

        // Assert that the user is no longer present
        assertFalse(deletedUser.isPresent(), "Deleted user should not be found");
    }
}
