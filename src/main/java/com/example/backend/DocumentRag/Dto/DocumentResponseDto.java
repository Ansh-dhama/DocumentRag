package com.example.backend.DocumentRag.Dto;

import com.example.backend.DocumentRag.Entity.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentResponseDto {
    private UUID uuid;
    private String fileName;
    private Long fileSize;
    private DocumentStatus documentStatus;
    private Integer chunkSize;

}
