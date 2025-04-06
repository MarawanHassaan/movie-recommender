package com.contenetwise.recommender.repository;

import com.contenetwise.recommender.domain.Genre;
import com.contenetwise.recommender.domain.Movie;
import com.contenetwise.recommender.domain.Ranking;
import com.contenetwise.recommender.repositories.GenreRepository;
import com.contenetwise.recommender.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Genre actionGenre;
    private Genre comedyGenre;
    private Movie actionMovie;
    private Movie comedyMovie;

    @BeforeEach
    void setUp() {
        // Initialize genres
        actionGenre = Genre.builder().name("Action-test").build();
        comedyGenre = Genre.builder().name("Comedy-test").build();
        genreRepository.save(actionGenre);
        genreRepository.save(comedyGenre);

        // Initialize movies
        actionMovie = Movie.builder().title("Action Movie").genres(new HashSet<>()).build();
        comedyMovie = Movie.builder().title("Comedy Movie").genres(new HashSet<>()).build();

        actionMovie.getGenres().add(actionGenre);
        comedyMovie.getGenres().add(comedyGenre);

        movieRepository.save(actionMovie);
        movieRepository.save(comedyMovie);
    }

    @Test
    void testFindByGenre() {
        // Test find by genre
        List<Movie> foundMovies = movieRepository.findByGenre("Action-test");

        assertEquals(1, foundMovies.size(), "There should be one movie with Action genre");
        assertEquals("Action Movie", foundMovies.get(0).getTitle(), "The movie should have the correct title");
    }

    @Test
    void testFindByGenreNames() {
        Set<String> genres = new HashSet<>();
        genres.add("Action-test");
        genres.add("Comedy-test");

        List<Movie> foundMovies = movieRepository.findByGenreNames(genres);

        assertEquals(2, foundMovies.size(), "There should be two movies with Action or Comedy genre");
    }

    @Test
    void testFindByTitleIgnoreCase() {
        List<Movie> foundMovies = movieRepository.findByTitleIgnoreCase("action movie");

        assertEquals(1, foundMovies.size(), "There should be one movie with title 'Action Movie' ignoring case");
        assertEquals("Action Movie", foundMovies.get(0).getTitle(), "The movie should have the correct title");
    }

    @Test
    void testFindByGenres() {
        Set<String> genres = new HashSet<>();
        genres.add("Action-test");

        List<Movie> foundMovies = movieRepository.findByGenres(genres);

        assertEquals(1, foundMovies.size(), "There should be one movie with the Action genre");
        assertEquals("Action Movie", foundMovies.get(0).getTitle(), "The movie should have the correct title");
    }

//    @Test
//    void testFindByTitleContainingIgnoreCase() {
//        List<Movie> foundMovies = movieRepository.findByTitleContainingIgnoreCase("movie");
//
//        assertEquals(2, foundMovies.size(), "There should be two movies containing 'movie' in the title");
//        assertTrue(foundMovies.stream().anyMatch(movie -> movie.getTitle().equals("Action Movie")));
//        assertTrue(foundMovies.stream().anyMatch(movie -> movie.getTitle().equals("Comedy Movie")));
//    }
}
