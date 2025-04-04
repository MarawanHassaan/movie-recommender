package com.contenetwise.recommender.domain;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Initialize User object
        user = User.builder()
                .id(1L)
                .username("john_doe")
                .build();
    }

    @Test
    void testUserConstruction() {
        // Validate that the User object is correctly constructed
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("john_doe", user.getUsername());
    }



    @Test
    void testUserEqualsAndHashCode() {
        // Testing equals and hashCode for correct entity comparison
        User user2 = User.builder()
                .id(1L)
                .username("john_doe")
                .build();

        User user3 = User.builder()
                .id(2L)
                .username("jane_doe")
                .build();

        // Same values should be equal
        assertEquals(user.getUsername(), user2.getUsername());

        // Different values should not be equal
        assertNotEquals(user.getUsername(), user3.getUsername());
    }

    @Test
    void testUserSetUsername() {
        // Validate that the 'username' field can be set and retrieved correctly
        user.setUsername("new_username");
        assertEquals("new_username", user.getUsername());
    }

    @Test
    void testUserId() {
        // Validate that the 'id' field is set and retrieved correctly
        user.setId(2L);
        assertEquals(2L, user.getId());
    }


}

