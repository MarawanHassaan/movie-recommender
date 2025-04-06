package com.contenetwise.recommender.repository;

import com.contenetwise.recommender.domain.Genre;
import com.contenetwise.recommender.repositories.GenreRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    private Genre actionGenre;

    @BeforeEach
    void setUp() {
        // Initialize Genre
        actionGenre = Genre.builder()
                .name("Action-test2")
                .build();
        genreRepository.save(actionGenre); // Save the genre for testing
    }

    @Test
    void testFindByNameShouldReturnGenreWhenExists() {
        // Find the Genre by name
        Optional<Genre> foundGenre = genreRepository.findByName("Action-test2");

        // Assert that the genre is present and matches the expected name
        assertTrue(foundGenre.isPresent(), "Genre should be found");
        assertEquals("Action-test2", foundGenre.get().getName(), "Genre name should match");
    }

    @Test
    void testFindByNameShouldReturnEmptyWhenNotExists() {
        Optional<Genre> foundGenre = genreRepository.findByName("NonExistent");

        assertFalse(foundGenre.isPresent(), "Genre should not be found");
    }

    @Test
    void testSaveGenre() {
        // Create a new genre and save it
        Genre comedyGenre = Genre.builder()
                .name("Comedy")
                .build();

        Genre savedGenre = genreRepository.save(comedyGenre);

        assertNotNull(savedGenre.getId(), "Saved genre should have a generated ID");
        assertEquals("Comedy", savedGenre.getName(), "Saved genre name should match");
    }

    @Test
    void testDeleteGenre() {
        // Save a genre and delete it
        Genre genreToDelete = Genre.builder()
                .name("Horror")
                .build();

        genreRepository.save(genreToDelete);
        genreRepository.delete(genreToDelete);

        Optional<Genre> deletedGenre = genreRepository.findByName("Horror");

        assertFalse(deletedGenre.isPresent(), "Deleted genre should not be found");
    }

}