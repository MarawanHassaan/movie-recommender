package com.contenetwise.recommender.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseDTO {
    private List<MovieRequest> movies;
}