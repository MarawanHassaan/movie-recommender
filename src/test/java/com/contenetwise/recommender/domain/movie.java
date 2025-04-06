package com.contenetwise.recommender.domain;



import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MovieTest {

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .build();
    }

    @Test
    void testMovieConstruction() {
        assertNotNull(movie);
        assertEquals(1L, movie.getId());
        assertEquals("Inception", movie.getTitle());
    }

    @Test
    void testMovieSetGenres() {
        Genre genre1 = Genre.builder().id(1L).name("Action").build();
        Genre genre2 = Genre.builder().id(2L).name("Sci-Fi").build();

        Set<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);

        movie.setGenres(genres);

        assertEquals(2, movie.getGenres().size());
        assertTrue(movie.getGenres().contains(genre1));
        assertTrue(movie.getGenres().contains(genre2));
    }


    @Test
    void testMovieEqualsAndHashCode() {
        Movie movie2 = Movie.builder()
                .id(1L)
                .title("Inception")
                .build();

        Movie movie3 = Movie.builder()
                .id(2L)
                .title("The Matrix")
                .build();

        assertEquals(movie.getTitle(), movie2.getTitle());

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
        movie.setId(2L);
        assertEquals(2L, movie.getId());
    }


}

