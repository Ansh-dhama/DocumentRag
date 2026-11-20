package com.example.backend.DocumentRag.Dto;

import com.drew.lang.annotations.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SerachRequestDto {

     @NotBlank(message = "Query could not be null")
     private String query;

     private UUID documentId;

     private Integer topK;

     private Double similaritySearch;

}
