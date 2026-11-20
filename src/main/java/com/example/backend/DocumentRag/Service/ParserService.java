package com.example.backend.DocumentRag.Service;

import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ParserService {
    List<Document> parse(MultipartFile file);
}
