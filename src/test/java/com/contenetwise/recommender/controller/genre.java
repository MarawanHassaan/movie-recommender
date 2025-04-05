package com.contenetwise.recommender.controller;


import com.contenetwise.recommender.controllers.GenreController;
import com.contenetwise.recommender.domain.Genre;
import com.contenetwise.recommender.repositories.GenreRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
class GenreControllerTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreController genreController;

    private MockMvc mockMvc;
    private Genre genre;

    @BeforeEach
    void setUp() {
        genre = Genre.builder()
                .id(1L)
                .name("Action")
                .build();

        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(genreController).build();
    }

    @Test
    void testCreateGenreShouldReturnCreatedGenre() throws Exception {
        when(genreRepository.findByName(any())).thenReturn(Optional.empty());
        when(genreRepository.save(any(Genre.class))).thenReturn(genre);

        mockMvc.perform(post("/api/genres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Action\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Action"));
    }

    @Test
    void testCreateGenreShouldReturnConflictIfGenreExists() throws Exception {
        when(genreRepository.findByName(any())).thenReturn(Optional.of(genre));

        mockMvc.perform(post("/api/genres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Action\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.name").value("Action"));
    }

    @Test
    void testCreateGenreShouldReturnBadRequestIfNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/genres/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllGenres() throws Exception {
        when(genreRepository.findAll()).thenReturn(List.of(genre));

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Action"));
    }

    @Test
    void testGetGenreByIdShouldReturnGenreWhenExists() throws Exception {
        when(genreRepository.findById(anyLong())).thenReturn(Optional.of(genre));

        mockMvc.perform(get("/api/genres/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Action"));
    }

    @Test
    void testGetGenreByIdShouldReturnNotFoundWhenNotExists() throws Exception {
        when(genreRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/genres/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateGenreShouldReturnUpdatedGenre() throws Exception {
        Genre updatedGenre = Genre.builder().name("Adventure").build();
        when(genreRepository.findById(anyLong())).thenReturn(Optional.of(genre));
        when(genreRepository.save(any(Genre.class))).thenReturn(updatedGenre);

        mockMvc.perform(put("/api/genres/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Adventure\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Adventure"));
    }

    @Test
    void testUpdateGenreShouldReturnNotFoundWhenGenreDoesNotExist() throws Exception {
        when(genreRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/genres/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Adventure\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteGenreShouldReturnSuccessMessage() throws Exception {
        when(genreRepository.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/genres/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Genre deleted successfully."));
    }

    @Test
    void testDeleteGenreShouldReturnNotFoundWhenGenreDoesNotExist() throws Exception {
        when(genreRepository.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/genres/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}

