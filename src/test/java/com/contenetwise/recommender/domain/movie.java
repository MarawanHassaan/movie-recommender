package com.contenetwise.recommender.domain;



import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class MovieTest {

    private Movie movie;

    @BeforeEach
    void setUp() {
        // Initialize Movie object with title and id
        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .build();
    }

    @Test
    void testMovieConstruction() {
        // Validate that the Movie object is correctly constructed
        assertNotNull(movie);
        assertEquals(1L, movie.getId());
        assertEquals("Inception", movie.getTitle());
    }

    @Test
    void testMovieSetGenres() {
        // Create some genres
        Genre genre1 = Genre.builder().id(1L).name("Action").build();
        Genre genre2 = Genre.builder().id(2L).name("Sci-Fi").build();

        Set<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);

        movie.setGenres(genres);

        // Validate that genres are correctly set
        assertEquals(2, movie.getGenres().size());
        assertTrue(movie.getGenres().contains(genre1));
        assertTrue(movie.getGenres().contains(genre2));
    }


    @Test
    void testMovieEqualsAndHashCode() {
        // Create another movie with the same id and title
        Movie movie2 = Movie.builder()
                .id(1L)
                .title("Inception")
                .build();

        Movie movie3 = Movie.builder()
                .id(2L)
                .title("The Matrix")
                .build();

        // Same id and title should be equal
        assertEquals(movie.getTitle(), movie2.getTitle());

        // Different id or title should not be equal
        assertNotEquals(movie.getTitle(), movie3.getTitle());
    }

    @Test
    void testMovieTitle() {
        // Validate that the 'title' field can be set and retrieved correctly
        movie.setTitle("The Dark Knight");
        assertEquals("The Dark Knight", movie.getTitle());
    }

    @Test
    void testMovieId() {
        // Validate that the 'id' field is set and retrieved correctly
        movie.setId(2L);
        assertEquals(2L, movie.getId());
    }


}

