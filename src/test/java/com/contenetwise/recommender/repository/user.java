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
@Transactional
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // Initialize a user
        user = User.builder()
                .username("john_doe")
                .build();
        userRepository.save(user); // Save the user for testing
    }

    @Test
    void testFindByUsernameShouldReturnUserWhenExists() {
        // Find the user by username
        Optional<User> foundUser = userRepository.findByUsername("john_doe");

        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals("john_doe", foundUser.get().getUsername(), "Username should match");
    }

    @Test
    void testFindByUsernameShouldReturnEmptyWhenNotExists() {
        Optional<User> foundUser = userRepository.findByUsername("non_existent_user");

        assertFalse(foundUser.isPresent(), "User should not be found");
    }

    @Test
    void testSaveUser() {
        User newUser = User.builder()
                .username("jane_doe")
                .build();

        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser.getId(), "Saved user should have a generated ID");
        assertEquals("jane_doe", savedUser.getUsername(), "Username should match");
    }

    @Test
    void testDeleteUser() {
        userRepository.delete(user);

        Optional<User> deletedUser = userRepository.findByUsername("john_doe");

        assertFalse(deletedUser.isPresent(), "Deleted user should not be found");
    }
}
