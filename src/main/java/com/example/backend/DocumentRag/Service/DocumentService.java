package com.example.backend.DocumentRag.Service;

import com.example.backend.DocumentRag.Dto.DocumentResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    DocumentResponseDto uploadAndProcess(MultipartFile file);
}
