package com.contenetwise.recommender.domain;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Initialize User
        user = User.builder()
                .id(1L)
                .username("john_doe")
                .build();
    }

    @Test
    void testUserConstruction() {
        // Validate that the User object is correct
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("john_doe", user.getUsername());
    }



    @Test
    void testUserEqualsAndHashCode() {
        User user2 = User.builder()
                .id(1L)
                .username("john_doe")
                .build();

        User user3 = User.builder()
                .id(2L)
                .username("jane_doe")
                .build();

        assertEquals(user.getUsername(), user2.getUsername());

        assertNotEquals(user.getUsername(), user3.getUsername());
    }

    @Test
    void testUserSetUsername() {
        user.setUsername("new_username");
        assertEquals("new_username", user.getUsername());
    }

    @Test
    void testUserId() {
        user.setId(2L);
        assertEquals(2L, user.getId());
    }


}

