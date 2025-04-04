package com.contenetwise.recommender.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MovieRequest {
    private String title;
    private Set<String> genres;
}