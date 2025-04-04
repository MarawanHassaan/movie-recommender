package com.contenetwise.recommender.dto;

import com.contenetwise.recommender.domain.Ranking;
import lombok.Getter;
import lombok.Setter;

import java.util.stream.Collectors;

@Getter
@Setter
public class RankingDTOResponse {
    private MovieRequest movie;
    private Integer rank1;
    private Integer rank2;

    public RankingDTOResponse(Ranking ranking) {
        this.movie = new MovieRequest();
        this.movie.setTitle(ranking.getMovie().getTitle());
        this.movie.setGenres(
                ranking.getMovie().getGenres()
                        .stream()
                        .map(genre -> genre.getName())
                        .collect(Collectors.toSet())
        );
        this.rank1 = ranking.getRank1();
        this.rank2 = ranking.getRank2();
    }
}
