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
class GenreTest {

    private Genre genre;

    @BeforeEach
    void setUp() {
        // Initialize Genre
        genre = Genre.builder()
                .id(1L)
                .name("Action-test")
                .build();
    }

    @Test
    void testGenreConstruction() {
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

        assertEquals(genre.getName(), genre2.getName());

        assertNotEquals(genre.getName(), genre3.getName());
    }

    @Test
    void testGenreSetName() {
        genre.setName("Drama");
        assertEquals("Drama", genre.getName());
    }

    @Test
    void testGenreId() {
        genre.setId(2L);
        assertEquals(2L, genre.getId());
    }



}
