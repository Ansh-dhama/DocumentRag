package com.example.backend.DocumentRag.ServiceImpl;

import com.example.backend.DocumentRag.Configuration.AppProperties;
import com.example.backend.DocumentRag.Entity.DocumentMetadata;
import com.example.backend.DocumentRag.Entity.DocumentStatus;
import com.example.backend.DocumentRag.Exception.DocumentException;
import com.example.backend.DocumentRag.Repo.DocumentMetadataRepo;
import com.example.backend.DocumentRag.Service.IngestionService;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IngestionServiceImpl implements IngestionService {
    private final static Logger logger = LoggerFactory.getLogger(IngestionServiceImpl.class);
    private final VectorStore vectorStore;
    private final DocumentMetadataRepo documentMetadataRepo;
    private final AppProperties appProperties;

public IngestionServiceImpl(VectorStore vectorStore, DocumentMetadataRepo documentMetadataRepo, AppProperties appProperties) {
        this.vectorStore = vectorStore;
        this.documentMetadataRepo = documentMetadataRepo;
        this.appProperties = appProperties;
}

    @Override
    public int ingest(DocumentMetadata documentMetadata, List<Document> parseDocs) {
        logger.info("Ingestion started {}",documentMetadata.getId());

        try{
            documentMetadata.setStatus(DocumentStatus.PROCESSING);
            documentMetadata.setTotalPages(parseDocs.size());

            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(appProperties.getRagProperties().getChunkSize())
                    .withMinChunkSizeChars(
                            appProperties.getRagProperties().getMinChunkSizeChar())
                    .withMinChunkLengthToEmbed(
                            appProperties.getRagProperties().getMinChunkLenToEmbed())
                    .withMaxNumChunks(
                            appProperties.getRagProperties().getMaxNumChunks())
                    .build();

            List<Document> chunks = splitter.split(parseDocs);

            if (chunks.isEmpty()) {
                documentMetadata.setStatus(DocumentStatus.FAILED);
                documentMetadata.setError(
                        "Document appears to be empty or unreadable"
                );

                documentMetadataRepo.save(documentMetadata);

                return 0;
            }

            List<Document> enrichedChunks = new ArrayList<>();

            for (Document document : chunks) {
                Map<String, Object> map = new HashMap<>();

                document.getMetadata().forEach((key, value) -> {
                    if (key != null && value != null) {
                        map.put(key, value);
                    }
                });
                if (documentMetadata.getId() == null) {
                    throw new IllegalStateException(
                            "Document metadata ID is null. Save metadata before ingestion."
                    );
                }
                map.put("documentId", documentMetadata.getId());
                map.put("filename", documentMetadata.getFilename());
                map.put("contentType", documentMetadata.getContentType());
                Document enrichedDocument =
                        new Document(document.getText(), map);

                enrichedChunks.add(enrichedDocument);
            }

            logger.info("Writing {} vector chunks to PGVector",
                    enrichedChunks.size());

            vectorStore.add(enrichedChunks);

            documentMetadata.setStatus(DocumentStatus.INDEXED);
            documentMetadata.setError(null);
            documentMetadataRepo.save(documentMetadata);

            return enrichedChunks.size();
         }catch (Exception e) {

            logger.error(
                    "Document ingestion failed for ID: {}",
                    documentMetadata.getId(),
                    e
            );

            documentMetadata.setStatus(DocumentStatus.FAILED);
            documentMetadata.setError("Document indexing failed");

            documentMetadataRepo.save(documentMetadata);

            throw new DocumentException(
                    "Failed to index document: " + e.getMessage()
            );
        }
    }
}
