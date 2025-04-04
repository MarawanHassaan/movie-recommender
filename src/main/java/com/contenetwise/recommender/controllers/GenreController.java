package com.contenetwise.recommender.controllers;

import com.contenetwise.recommender.domain.Genre;
import com.contenetwise.recommender.dto.GenreDTOResponse;
import com.contenetwise.recommender.repositories.GenreRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genre API", description = "Operations related to genre")

public class GenreController {

    private final GenreRepository genreRepository;
    private static final Logger logger = LoggerFactory.getLogger(GenreController.class);

    public GenreController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Operation(summary = "Create a new genre", description = "Add a new genre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful creation of genre"),
            @ApiResponse(responseCode = "400", description = "Invalid genre name")
    })
    @PostMapping("/create")
    public ResponseEntity<GenreDTOResponse> createGenre(@RequestBody GenreDTOResponse genre) {
        logger.info("Create request received for genre with name: {}", genre.getName());

        // Validate the genre name
        if (genre.getName() == null || genre.getName().isBlank()) {
            logger.warn("Invalid genre name provided: '{}'", genre.getName());
            return ResponseEntity.badRequest().build();
        }

        // Check if genre already exists
        Optional<Genre> existingGenre = genreRepository.findByName(genre.getName());
        if (existingGenre.isPresent()) {
            logger.warn("Genre with name '{}' already exists. Creation aborted.", genre.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new GenreDTOResponse(existingGenre.get().getName())); // Return existing genre
        }

        // Convert GenreDTOResponse to Genre entity and save
        Genre genreToSave = new Genre();
        genreToSave.setName(genre.getName());

        Genre savedGenre = genreRepository.save(genreToSave);

        logger.info("Genre created successfully with name: {}", savedGenre.getName());
        return ResponseEntity.ok(new GenreDTOResponse(savedGenre.getName()));
    }

    @Operation(summary = "Get a list of all genres", description = "Retrieve the list of genres")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of genres"),
    })
    @GetMapping
    public ResponseEntity<List<GenreDTOResponse>> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        logger.info("Get request called for all genres");
        List<GenreDTOResponse> genreDTOs = genres.stream()
                .map(genre -> new GenreDTOResponse(genre.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(genreDTOs);
    }

    @Operation(summary = "Get a genre by ID", description = "Retrieve genre by specifying the ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GenreDTOResponse> getGenreById(@PathVariable Long id) {
        logger.info("Request called for genre with ID: {}", id);
        return genreRepository.findById(id)
                .map(genre -> {
                    logger.info("Genre found: {} with ID: {}", genre.getName(),id);
                    return ResponseEntity.ok(new GenreDTOResponse(genre.getName()));
                })
                .orElseGet(() -> {
                    logger.warn("Genre with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Modify a genre by ID", description = "Update genre by specifying the ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful update of genre"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GenreDTOResponse> updateGenre(@PathVariable Long id, @RequestBody GenreDTOResponse updatedGenre) {
        logger.info("Update request received for genre with ID: {}", id);
        return genreRepository.findById(id)
                .map(existingGenre -> {
                    logger.info("Genre found with ID: {}. Updating name from '{}' to '{}'",
                            id, existingGenre.getName(), updatedGenre.getName());

                    existingGenre.setName(updatedGenre.getName());
                    Genre savedGenre = genreRepository.save(existingGenre);

                    logger.info("Genre updated successfully with ID: {}", id);
                    return ResponseEntity.ok(new GenreDTOResponse(savedGenre.getName()));
                })
                .orElseGet(() -> {
                    logger.warn("Genre with ID {} not found. Update failed.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Delete a genre by ID", description = "Remove genre by specifying the ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful deletion of genre"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Long id) {
        logger.info("Delete request received for genre with ID: {}", id);
        if (!genreRepository.existsById(id)) {
            logger.warn("Genre with ID {} not found. Deletion aborted.", id);
            return ResponseEntity.notFound().build();
        }

        genreRepository.deleteById(id);
        logger.info("Genre with ID {} deleted successfully.", id);
        return ResponseEntity.ok("Genre deleted successfully.");
    }
}
