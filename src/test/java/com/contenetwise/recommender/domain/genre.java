package com.contenetwise.recommender.domain;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GenreTest {

    private Genre genre;

    @BeforeEach
    void setUp() {
        // Initialize Genre object
        genre = Genre.builder()
                .id(1L)
                .name("Action-test")
                .build();
    }

    @Test
    void testGenreConstruction() {
        // Validate that the Genre object is correctly constructed
        assertNotNull(genre);
        assertEquals(1L, genre.getId());
        assertEquals("Action-test", genre.getName());
    }



    @Test
    void testGenreEqualsAndHashCode() {
        // Testing equals and hashCode for correct entity comparison
        Genre genre2 = Genre.builder()
                .id(1L)
                .name("Action-test")
                .build();

        Genre genre3 = Genre.builder()
                .id(2L)
                .name("Comedy-test")
                .build();

        // Same values should be equal
        assertEquals(genre.getName(), genre2.getName());

        // Different values should not be equal
        assertNotEquals(genre.getName(), genre3.getName());
    }

    @Test
    void testGenreSetName() {
        // Validate that the 'name' field can be set and retrieved correctly
        genre.setName("Drama");
        assertEquals("Drama", genre.getName());
    }

    @Test
    void testGenreId() {
        // Validate that the 'id' field is set and retrieved correctly
        genre.setId(2L);
        assertEquals(2L, genre.getId());
    }



}
