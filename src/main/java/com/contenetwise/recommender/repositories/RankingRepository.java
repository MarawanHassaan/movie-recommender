package com.contenetwise.recommender.repositories;

import com.contenetwise.recommender.domain.Movie;
import com.contenetwise.recommender.domain.Ranking;
import com.contenetwise.recommender.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    // Retrieve all rankings for a user
    @Query("SELECT r FROM Ranking r WHERE r.user.id = :userId")
    List<Ranking> findByUser(@Param("userId") Long userId);

    // Retrieve only rank1 for a user
    @Query("SELECT r FROM Ranking r WHERE r.user.id = :userId AND r.rank1 IS NOT NULL")
    List<Ranking> findByUserRank1Only(@Param("userId") Long userId);

    // Retrieve only rank2 for a user
    @Query("SELECT r FROM Ranking r WHERE r.user.id = :userId AND r.rank2 IS NOT NULL")
    List<Ranking> findByUserRank2Only(@Param("userId") Long userId);


    Optional<Ranking> findByUserAndMovie(User user, Movie movie);

    List<Ranking> findByUserAndRank1GreaterThanEqual(User user, int minRating);
    int countByMovie(Movie movie);
}
