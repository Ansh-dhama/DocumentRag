package com.example.backend.DocumentRag.Service;

import com.example.backend.DocumentRag.Entity.DocumentMetadata;
import org.springframework.ai.document.Document;

import java.util.List;

public interface IngestionService {
    int ingest(DocumentMetadata documentMetadata, List<Document> parseDocs);
}
