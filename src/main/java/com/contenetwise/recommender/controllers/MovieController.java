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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    public MovieController(MovieRepository movieRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }


    @Operation(summary = "Filter movies by genres", description = "Retrieve a list of all movies belonging to specific genres")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Movies not found")
    })
    @GetMapping("/by-genre")
    @Transactional
    public ResponseEntity<ResponseDTO> getMoviesByGenre(@RequestParam String genre) {
        logger.info("Request received to get movies for genre: {}", genre);
        List<Movie> movies = movieRepository.findByGenre(genre);
        if (movies.isEmpty()) {
            logger.warn("No movies found for genre: {}", genre);
            return ResponseEntity.noContent().build();
        }
        logger.info("Found {} movies for genre: {}", movies.size(), genre);
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
            @ApiResponse(responseCode = "404", description = "Movies not found")
    })
    @GetMapping("/by-min-ranking")
    public ResponseEntity<ResponseDTO> getMoviesByMinRanking(@RequestParam double minRanking) {
        logger.info("Request received to get movies with minimum ranking: {}", minRanking);
        List<Movie> movies = movieRepository.findByMinRanking(minRanking);
        if (movies.isEmpty()) {
            logger.warn("No movies found with minimum ranking: {}", minRanking);
            return ResponseEntity.noContent().build();
        }
        logger.info("Found {} movies with minimum ranking: {}", movies.size(), minRanking);
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


    @GetMapping("/by-max-ranking")
    public ResponseEntity<ResponseDTO> getMoviesByMaxRanking(@RequestParam double maxRanking) {
        logger.info("Request received to get movies with maximum ranking: {}", maxRanking);
        List<Movie> movies = movieRepository.findByMaxRanking(maxRanking);
        if (movies.isEmpty()) {
            logger.warn("No movies found with maximum ranking: {}", maxRanking);
            return ResponseEntity.noContent().build();
        }

        logger.info("Found {} movies with maximum ranking: {}", movies.size(), maxRanking);
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

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMovies(movieDTOs);

        return ResponseEntity.ok(responseDTO);
    }


    @Operation(summary = "Search movies", description = "Return a search result based on title or genre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Movies not found")
    })
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) String keyword) {
        logger.info("Search request received with parameters - title: {}, genres: {}, keyword: {}",
                title, genres, keyword);
        List<Movie> results = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            logger.info("Searching for movies with title: {}", title);
            results.addAll(movieRepository.findByTitleIgnoreCase(title));
        }

        if (genres != null && !genres.isEmpty()) {
            logger.info("Searching for movies with genres: {}", genres);
            results.addAll(movieRepository.findByGenres(new HashSet<>(genres)));
        }

        if (keyword != null && !keyword.isBlank()) {
            logger.info("Searching for movies containing keyword: {}", keyword);
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

        logger.info("Found {} movies for the search results.", movieDTOs.size());
        // Prepare ResponseDTO
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMovies(movieDTOs);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Create a new movie", description = "Add a new movie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "movie is null or title is blank")
    })
    @PostMapping("/create")
    public ResponseEntity<MovieRequest> createMovie(@RequestBody MovieRequest movieRequest) {
        logger.info("Received request to create movie with title: {}", movieRequest.getTitle());
        if (movieRequest.getTitle() == null || movieRequest.getTitle().isBlank()) {
            logger.warn("Movie creation failed: Title is blank or null.");
            return ResponseEntity.badRequest().build();
        }

        // Find or create genres
        logger.info("Finding or creating genres for the movie: {}", movieRequest.getGenres());
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
        logger.info("Movie created successfully with title: {}", savedMovie.getTitle());
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Get a list of movies available", description = "Retrieval a list of movie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
    })
    @GetMapping
    public ResponseEntity<List<MovieRequest>> getAllMovies() {
        logger.info("Request received to get movies");

        List<Movie> movies = movieRepository.findAll();

        List<MovieRequest> movieRequests = movies.stream().map(movie -> {
            MovieRequest dto = new MovieRequest();
            dto.setTitle(movie.getTitle());
            Set<String> genres = movie.getGenres().stream()
                    .map(g -> g.getName())
                    .collect(Collectors.toSet());
            dto.setGenres(genres);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(movieRequests);
    }

    @Operation(summary = "Get a movie by ID", description = "Retrieval of movie with ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Movies not found")

    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieRequest> getMovieById(@PathVariable Long id) {
        logger.info("Request received to get movie with id {}", id);
        return movieRepository.findById(id)
                .map(movie -> {
                    MovieRequest dto = new MovieRequest();
                    dto.setTitle(movie.getTitle());
                    Set<String> genres = movie.getGenres().stream()
                            .map(g -> g.getName())
                            .collect(Collectors.toSet());
                    dto.setGenres(genres);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a movie", description = "Delete of movie with ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful deletion"),
            @ApiResponse(responseCode = "404", description = "Movies not found")

    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        logger.info("Request received to delete movie with id {}", id);
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return ResponseEntity.ok("Movie deleted successfully.");
            // HTTP 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // HTTP 404 Not Found
        }
    }


}
