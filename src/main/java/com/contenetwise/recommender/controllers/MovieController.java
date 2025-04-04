package com.contenetwise.recommender.controllers;

import com.contenetwise.recommender.domain.Genre;
import com.contenetwise.recommender.domain.Movie;
import com.contenetwise.recommender.dto.MovieRequest;
import com.contenetwise.recommender.dto.ResponseDTO;
import com.contenetwise.recommender.repositories.GenreRepository;
import com.contenetwise.recommender.repositories.MovieRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movie API", description = "Operations related to movies")
public class MovieController {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    public MovieController(MovieRepository movieRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }


    @Operation(summary = "Filter movies by genres", description = "Retrieve a list of all movies belonging to specific genres")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @GetMapping("/by-genre")
    @Transactional
    public ResponseEntity<ResponseDTO> getMoviesByGenre(@RequestParam String genre) {
        List<Movie> movies = movieRepository.findByGenre(genre);
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Convert the list of Movie entities to MovieDTOs
        List<MovieRequest> movieDTOs = movies.stream()
                .map(movie -> {
                    MovieRequest movieDTO = new MovieRequest();
                    movieDTO.setTitle(movie.getTitle());
                    movieDTO.setGenres(movie.getGenres().stream()
                            .map(Genre::getName)
                            .collect(Collectors.toSet()));
                    return movieDTO;
                })
                .collect(Collectors.toList());

        // Create ResponseDTO and add message
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMovies(movieDTOs);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Filter movies by ranking", description = "Retrieve a list of all movies rated higher than the passed ranking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @GetMapping("/by-min-ranking")
    public ResponseEntity<ResponseDTO> getMoviesByMinRanking(@RequestParam double minRanking) {
        List<Movie> movies = movieRepository.findByMinRanking(minRanking);
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<MovieRequest> movieDTOs = movies.stream()
                .map(movie -> {
                    MovieRequest movieDTO = new MovieRequest();
                    movieDTO.setTitle(movie.getTitle());
                    movieDTO.setGenres(movie.getGenres().stream()
                            .map(Genre::getName)
                            .collect(Collectors.toSet()));
                    return movieDTO;
                })
                .collect(Collectors.toList());

        // Create ResponseDTO and add message
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMovies(movieDTOs);

        return ResponseEntity.ok(responseDTO);
    }
    @Operation(summary = "Search movies", description = "Return a search result based on title or genre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) String keyword) {

        List<Movie> results = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            results.addAll(movieRepository.findByTitleIgnoreCase(title));
        }

        if (genres != null && !genres.isEmpty()) {
            results.addAll(movieRepository.findByGenres(new HashSet<>(genres)));
        }

        if (keyword != null && !keyword.isBlank()) {
            results.addAll(movieRepository.findByTitleContainingIgnoreCase(keyword));
        }

        // Remove duplicates
        List<Movie> distinctResults = results.stream().distinct().collect(Collectors.toList());

        List<MovieRequest> movieDTOs = distinctResults.stream()
                .map(movie -> {
                    MovieRequest movieDTO = new MovieRequest();
                    movieDTO.setTitle(movie.getTitle());
                    movieDTO.setGenres(movie.getGenres().stream()
                            .map(Genre::getName)
                            .collect(Collectors.toSet()));
                    return movieDTO;
                })
                .collect(Collectors.toList());

        // Prepare ResponseDTO
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMovies(movieDTOs);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Create a new movie", description = "Add a new movie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @PostMapping("/create")
    public ResponseEntity<MovieRequest> createMovie(@RequestBody MovieRequest movieRequest) {
        if (movieRequest.getTitle() == null || movieRequest.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Find or create genres
        Set<Genre> genres = movieRequest.getGenres().stream()
                .map(genreName -> genreRepository.findByName(genreName)
                        .orElseGet(() -> genreRepository.save(Genre.builder().name(genreName).build())))
                .collect(Collectors.toSet());

        // Create and save the movie
        Movie movie = new Movie();
        movie.setTitle(movieRequest.getTitle());
        movie.setGenres(genres);

        Movie savedMovie = movieRepository.save(movie);

        // Prepare response DTO without the 'id'
        MovieRequest responseDTO = new MovieRequest();
        responseDTO.setTitle(savedMovie.getTitle());
        responseDTO.setGenres(savedMovie.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet()));

        return ResponseEntity.ok(responseDTO);
    }


}
