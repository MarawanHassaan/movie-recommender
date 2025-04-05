package com.contenetwise.recommender.controllers;


import com.contenetwise.recommender.domain.Genre;
import com.contenetwise.recommender.domain.Movie;
import com.contenetwise.recommender.domain.Ranking;
import com.contenetwise.recommender.domain.User;
import com.contenetwise.recommender.dto.MovieRequest;
import com.contenetwise.recommender.dto.RankingDTOResponse;
import com.contenetwise.recommender.dto.ResponseDTO;
import com.contenetwise.recommender.repositories.MovieRepository;
import com.contenetwise.recommender.repositories.RankingRepository;
import com.contenetwise.recommender.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rankings")
@Tag(name = "Ranking API", description = "Operations related to rankings")

public class RankingController {

    private final RankingRepository rankingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private static final Logger logger = LoggerFactory.getLogger(RankingController.class);

    public RankingController(RankingRepository rankingRepository, UserRepository userRepository, MovieRepository movieRepository) {
        this.rankingRepository = rankingRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @Operation(summary = "Get the rankings for user by ID", description = "Retrieve a user's ranking history. The API has to provide an optional query\n" +
            "parameter to retrieve ratings only, views only or both")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RankingDTOResponse>> getUserRankings(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "both") String type) {

        logger.info("Request received for user rankings with userId: {} and type: {}", userId, type);
        List<Ranking> rankings;

        switch (type.toLowerCase()) {
            case "rank1":
                logger.info("Fetching rankings for userId: {} with rank type 'rank1'", userId);
                rankings = rankingRepository.findByUserRank1Only(userId);
                break;
            case "rank2":
                logger.info("Fetching rankings for userId: {} with rank type 'rank2'", userId);
                rankings = rankingRepository.findByUserRank2Only(userId);
                break;
            case "both":
            default:
                logger.info("Fetching both rank types for userId: {}", userId);
                rankings = rankingRepository.findByUser(userId);
                break;
        }

        if (rankings.isEmpty()) {
            logger.warn("No rankings found for userId: {} with type: {}", userId, type);
            return ResponseEntity.noContent().build();
        }
        logger.info("Found {} rankings for userId: {} with type: {}", rankings.size(), userId, type);
        List<RankingDTOResponse> rankingDTOs = rankings.stream()
                .map(RankingDTOResponse::new)
                .toList();

        return ResponseEntity.ok(rankingDTOs);
    }

    @Operation(summary = "Update or create a new ranking", description = "Ingest a new event for a movie by updating the user's ranking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @PostMapping("/user/{userId}/movie/{movieId}")
    public ResponseEntity<String> createOrUpdateRanking(
            @PathVariable Long userId,
            @PathVariable Long movieId,
            @RequestParam(required = false) Integer rank1,
            @RequestParam(required = false) Integer rank2) {

        logger.info("Received request to create or update ranking for userId: {} and movieId: {}", userId, movieId);
        // Validate rank1 and rank2 - at least one must be present, but not both
        if ((rank1 == null && rank2 == null) || (rank1 != null && rank2 != null)) {
            logger.warn("Bad request for userId: {} and movieId: {}. Both rank1 and rank2 are either null or both are provided.", userId, movieId);
            return ResponseEntity.badRequest().body("You must provide either rank1 or rank2, but not both.");
        }

        // Retrieve the user and movie
        logger.info("Retrieving user with userId: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        logger.info("Retrieving movie with movieId: {}", movieId);
        Movie movie = movieRepository.findById(movieId).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        if (movie == null) {
            return ResponseEntity.badRequest().body("Movie not found.");
        }

        // Check if the ranking already exists
        Ranking existingRanking = rankingRepository.findByUserAndMovie(user, movie).orElse(null);

        if (existingRanking != null) {
            // Update the existing ranking
            if (rank1 != null) {
                logger.info("Updated rank1 for userId: {} and movieId: {} to {}", userId, movieId, rank1);
                existingRanking.setRank1(rank1);
            }
            if (rank2 != null) {
                logger.info("Updated rank2 for userId: {} and movieId: {} to {}", userId, movieId, rank2);
                existingRanking.setRank2(rank2);
            }
            rankingRepository.save(existingRanking);
            return ResponseEntity.ok("Ranking updated successfully.");
        } else {
            // Create a new ranking
            Ranking newRanking = new Ranking();
            newRanking.setUser(user);
            newRanking.setMovie(movie);
            newRanking.setRank1(rank1);
            newRanking.setRank2(rank2);
            rankingRepository.save(newRanking);
            logger.info("New ranking created successfully for userId: {} and movieId: {}", userId, movieId);
            return ResponseEntity.ok("Ranking created successfully.");
        }
    }

    @Operation(summary = "Recommend a new movie to a user", description = "Retrieve a list of recommended movies similar to the user preference")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @GetMapping("/user/{userId}/recommendations")
    public ResponseEntity<ResponseDTO> recommendMovies(@PathVariable Long userId) {
        logger.info("Received request for movie recommendations for userId: {}", userId);
        // Get the user
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.warn("User with userId: {} not found. Returning bad request response.", userId);
            return ResponseEntity.badRequest().build();
        }

        // Get movies the user has rated highly (rank1 >= 4 or mapped rank2 >= 4)
        List<Ranking> highlyRatedRankings = rankingRepository.findByUser(userId)
                .stream()
                .filter(r -> isHighlyRated(r))
                .collect(Collectors.toList());

        if (highlyRatedRankings.isEmpty()) {
            logger.info("No highly rated rankings found for userId: {}. No recommendations available.", userId);
            return ResponseEntity.ok((ResponseDTO) Collections.emptyList()); // No recommendations if no high ratings
        }

        // Extract genres from highly rated movies
        Set<String> preferredGenres = highlyRatedRankings.stream()
                .flatMap(r -> r.getMovie().getGenres().stream()) // Extract Genre objects
                .map(Genre::getName) // Convert Genre objects to names
                .collect(Collectors.toSet());

        // Find recommended movies by genre
        List<Movie> recommendedMovies = movieRepository.findByGenreNames(preferredGenres);

        // Exclude movies the user has already rated (either rank1 or rank2 exists)
        Set<Long> ratedMovieIds = rankingRepository.findByUser(userId).stream()
                .map(r -> r.getMovie().getId())
                .collect(Collectors.toSet());

        List<Movie> filteredMovies = recommendedMovies.stream()
                .filter(movie -> !ratedMovieIds.contains(movie.getId())) // Exclude already rated
                .collect(Collectors.toList());

        // Optional: Sort by the number of times the movie was rated (popularity)
        logger.info("Sorting recommended movies based on popularity for userId: {}", userId);
        filteredMovies.sort(Comparator.comparingInt(movie -> rankingRepository.countByMovie(movie)));

        List<MovieRequest> movieRequests = filteredMovies.stream()
                .map(movie -> {
                    MovieRequest dto = new MovieRequest();
                    dto.setTitle(movie.getTitle());
                    dto.setGenres(movie.getGenres().stream()
                            .map(Genre::getName)
                            .collect(Collectors.toSet()));
                    return dto;
                })
                .collect(Collectors.toList());

        // Wrap in ResponseDTO
        logger.info("Returning recommendations for userId: {} with {} movies", userId, movieRequests.size());
        ResponseDTO response = new ResponseDTO();
        response.setMovies(movieRequests);

        return ResponseEntity.ok(response);
    }

    private boolean isHighlyRated(Ranking ranking) {
        if (ranking.getRank1() != null && ranking.getRank1() >= 4) {
            return true;
        }
        if (ranking.getRank2() != null) {
            int mappedRank2 = mapRank2ToFiveScale(ranking.getRank2());
            return mappedRank2 >= 4;
        }
        return false;
    }

    /**
     * Maps rank2 (0-100) to a 1-5 scale.
     */
    private int mapRank2ToFiveScale(int rank2) {
        if (rank2 >= 81) return 5;
        if (rank2 >= 61) return 4;
        if (rank2 >= 41) return 3;
        if (rank2 >= 21) return 2;
        return 1;
    }
}

