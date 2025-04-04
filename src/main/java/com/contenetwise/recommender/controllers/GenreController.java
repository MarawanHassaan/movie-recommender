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
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @PostMapping("/create")
    public ResponseEntity<GenreDTOResponse> createGenre(@RequestBody Genre genre) {
        if (genre.getName() == null || genre.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Check if genre already exists
        Optional<Genre> existingGenre = genreRepository.findByName(genre.getName());
        if (existingGenre.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new GenreDTOResponse(existingGenre.get().getName())); // Convert to DTO
        }

        Genre savedGenre = genreRepository.save(genre);
        return ResponseEntity.ok(new GenreDTOResponse( savedGenre.getName())); // Convert to DTO
    }

    @Operation(summary = "Get a list of all genres", description = "Retrieve the list of genres")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @GetMapping
    public ResponseEntity<List<GenreDTOResponse>> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        logger.info("genres are called");
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
        return genreRepository.findById(id)
                .map(genre -> new GenreDTOResponse(genre.getName())) // Convert to DTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Modify a genre by ID", description = "Update genre by specifying the ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GenreDTOResponse> updateGenre(@PathVariable Long id, @RequestBody Genre updatedGenre) {
        return genreRepository.findById(id)
                .map(existingGenre -> {
                    existingGenre.setName(updatedGenre.getName());
                    Genre savedGenre = genreRepository.save(existingGenre);
                    return ResponseEntity.ok(new GenreDTOResponse(savedGenre.getName())); // Convert to DTO
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a genre by ID", description = "Remove genre by specifying the ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Long id) {
        if (!genreRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        genreRepository.deleteById(id);
        return ResponseEntity.ok("Genre deleted successfully.");
    }
}
