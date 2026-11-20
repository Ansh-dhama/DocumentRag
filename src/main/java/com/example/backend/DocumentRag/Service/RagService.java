package com.example.backend.DocumentRag.Service;

import com.example.backend.DocumentRag.Dto.SerachRequestDto;
import com.example.backend.DocumentRag.Dto.SerachResultDto;

public interface RagService {
      SerachResultDto serachSimilarChunks(SerachRequestDto serachRequestDto);
}
