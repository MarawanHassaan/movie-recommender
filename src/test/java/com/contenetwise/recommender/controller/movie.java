package com.contenetwise.recommender.controller;


import com.contenetwise.recommender.controllers.MovieController;
import com.contenetwise.recommender.domain.Genre;
import com.contenetwise.recommender.domain.Movie;
import com.contenetwise.recommender.dto.MovieRequest;
import com.contenetwise.recommender.repositories.MovieRepository;
import com.contenetwise.recommender.repositories.GenreRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class MovieControllerTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private MovieController movieController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();
    }

    @Test
    void testCreateMovieShouldReturnCreatedMovie() throws Exception {
        // Prepare mock data
        String genreName = "Action";
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName(genreName);

        MovieRequest movieRequest = new MovieRequest();
        movieRequest.setTitle("New Movie");
        movieRequest.setGenres(Set.of(genreName));

        // Mock the repository behavior
        when(genreRepository.findByName(anyString())).thenReturn(Optional.of(genre));
        when(movieRepository.save(any(Movie.class))).thenReturn(Movie.builder().id(1L).title("New Movie").genres(Set.of(genre)).build());

        // Perform POST request to create movie
        mockMvc.perform(post("/api/movies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Movie\", \"genres\":[\"Action\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Movie"));
                //.andExpect(jsonPath("$.genres[0].name").value("Action"));

        // Verify repository interactions
        verify(movieRepository, times(1)).save(any(Movie.class));
        verify(genreRepository, times(1)).findByName(anyString());
    }

    @Test
    void testCreateMovieShouldReturnBadRequestIfTitleIsBlank() throws Exception {
        mockMvc.perform(post("/api/movies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\", \"genres\":[\"Action\"]}"))
                .andExpect(status().isBadRequest());

        verify(movieRepository, never()).save(any(Movie.class));
    }

//    @Test
//    void testCreateMovieShouldCreateNewGenreWhenNotFound() throws Exception {
//        String genreName = "Action";
//        MovieRequest movieRequest = new MovieRequest();
//        movieRequest.setTitle("New Movie");
//        movieRequest.setGenres(Set.of(genreName));
//
//        Genre newGenre = new Genre();
//        newGenre.setName(genreName);
//
//        Movie movie = new Movie();
//        movie.setTitle("New Movie");
//        movie.setGenres(Set.of(newGenre));
//
//        // Mock repository behavior
//        when(genreRepository.findByName(anyString())).thenReturn(Optional.empty());
//        when(genreRepository.save(any(Genre.class))).thenReturn(newGenre);
//        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
//
//        // Perform POST request to create movie
//        mockMvc.perform(post("/api/movies/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"title\":\"New Movie\", \"genres\":[\"Action\"]}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("New Movie"));
//                //.andExpect(jsonPath("$.genres[0].name").value("Action"));
//
//        // Verify repository interactions
//        verify(movieRepository, times(1)).save(any(Movie.class));
//        verify(genreRepository, times(1)).findByName(anyString());
//        verify(genreRepository, times(1)).save(any(Genre.class));
//    }


}
