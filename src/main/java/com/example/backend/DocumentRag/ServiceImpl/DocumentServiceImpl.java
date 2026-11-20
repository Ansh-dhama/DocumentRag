package com.example.backend.DocumentRag.ServiceImpl;

import com.example.backend.DocumentRag.Dto.DocumentResponseDto;
import com.example.backend.DocumentRag.Entity.DocumentMetadata;
import com.example.backend.DocumentRag.Entity.DocumentStatus;
import com.example.backend.DocumentRag.Repo.DocumentMetadataRepo;
import com.example.backend.DocumentRag.Service.DocumentService;
import com.example.backend.DocumentRag.Service.IngestionService;
import com.example.backend.DocumentRag.Service.ParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final DocumentMetadataRepo documentMetadataRepo;
    private final ParserService parserService;
    private final IngestionService ingestionService;
    public DocumentServiceImpl(DocumentMetadataRepo documentMetadataRepo, DocumentMetadataRepo documentMetadataRepo1, ParserService parserService, IngestionService ingestionService) {
        this.documentMetadataRepo = documentMetadataRepo1;
        this.parserService = parserService;
        this.ingestionService = ingestionService;
    }

    @Override
    public DocumentResponseDto uploadAndProcess(MultipartFile file) {

        String fileName = file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "document";

        String contentType = file.getContentType() != null
                ? file.getContentType()
                : "application/octet-stream";

        // STEP 1: Create metadata
        DocumentMetadata metadata = DocumentMetadata.builder()
                .filename(fileName)
                .contentType(contentType)
                .status(DocumentStatus.UPLOADING)
                .fileSize(file.getSize())
                .build();

        // STEP 2: Save immediately and generate UUID
        metadata = documentMetadataRepo.saveAndFlush(metadata);

        // Verify generated ID
        if (metadata.getId() == null) {
            throw new IllegalStateException(
                    "Document ID was not generated"
            );
        }

        logger.info("Document metadata saved. ID: {}", metadata.getId());

        // STEP 3: Parse document
        List<Document> parsedDocs = parserService.parse(file);

        // STEP 4: Ingest document using generated ID
        int chunksCreated =
                ingestionService.ingest(metadata, parsedDocs);

        // STEP 5: Update total chunks
        metadata.setTotalChunks(chunksCreated);

        // STEP 6: Save final metadata
        DocumentMetadata savedMetadata =
                documentMetadataRepo.save(metadata);

        // STEP 7: Return response
        return DocumentResponseDto.builder()
                .fileSize(file.getSize())
                .fileName(fileName)
                .chunkSize(chunksCreated)
                .documentStatus(savedMetadata.getStatus())
                .build();
    }

}
