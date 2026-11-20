package com.example.backend.DocumentRag.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SerachResultDto {
    private String query;
    private int totalMatches;
    private List<CitationDto> matches;
}
