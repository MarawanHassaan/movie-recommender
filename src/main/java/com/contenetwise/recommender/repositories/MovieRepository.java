package com.contenetwise.recommender.repositories;

import com.contenetwise.recommender.domain.Genre;
import com.contenetwise.recommender.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    //Find movie by genre
    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.name = :genreName")
    List<Movie> findByGenre(@Param("genreName") String genreName);


    //Find movies rated higher a minimum number
    @Query("""
    SELECT m FROM Movie m\s
    JOIN m.rankings r\s
    WHERE r.rank1 IS NOT NULL OR r.rank2 IS NOT NULL
    GROUP BY m\s
    HAVING AVG(
        CASE\s
            WHEN r.rank1 IS NOT NULL THEN r.rank1\s
            WHEN r.rank2 IS NOT NULL THEN\s
                CASE\s
                    WHEN r.rank2 BETWEEN 0 AND 20 THEN 1
                    WHEN r.rank2 BETWEEN 21 AND 40 THEN 2
                    WHEN r.rank2 BETWEEN 41 AND 60 THEN 3
                    WHEN r.rank2 BETWEEN 61 AND 80 THEN 4
                    WHEN r.rank2 BETWEEN 81 AND 100 THEN 5
                    ELSE NULL
                END
            ELSE NULL
        END
    ) >= :minRanking
   \s""")
    List<Movie> findByMinRanking(@Param("minRanking") double minRanking);

    //Find movies rated lower a maximum number
    @Query("""
    SELECT m FROM Movie m
    JOIN m.rankings r
    WHERE r.rank1 IS NOT NULL OR r.rank2 IS NOT NULL
    GROUP BY m
    HAVING AVG(
        CASE
            WHEN r.rank1 IS NOT NULL THEN r.rank1
            WHEN r.rank2 IS NOT NULL THEN
                CASE
                    WHEN r.rank2 BETWEEN 0 AND 20 THEN 1
                    WHEN r.rank2 BETWEEN 21 AND 40 THEN 2
                    WHEN r.rank2 BETWEEN 41 AND 60 THEN 3
                    WHEN r.rank2 BETWEEN 61 AND 80 THEN 4
                    WHEN r.rank2 BETWEEN 81 AND 100 THEN 5
                    ELSE NULL
                END
            ELSE NULL
        END
    ) <= :maxRanking
""")
    List<Movie> findByMaxRanking(@Param("maxRanking") double maxRanking);



    //Find movies by genre names
    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.name IN :genreNames")
    List<Movie> findByGenreNames(@Param("genreNames") Set<String> genreNames);

    //Find movies with title name
    List<Movie> findByTitleIgnoreCase(String title);

    // Match movies by genres
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.name IN :genres")
    List<Movie> findByGenres(@Param("genres") Set<String> genres);

    // Find movies based on keyword
    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Movie> findByTitleContainingIgnoreCase(@Param("keyword") String keyword);

}
