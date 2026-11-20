package com.example.backend.DocumentRag.ServiceImpl;

import com.example.backend.DocumentRag.Configuration.AppProperties;
import com.example.backend.DocumentRag.Dto.CitationDto;
import com.example.backend.DocumentRag.Dto.SerachRequestDto;
import com.example.backend.DocumentRag.Dto.SerachResultDto;
import com.example.backend.DocumentRag.Service.RagService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RagServiceImpl implements RagService {

    private AppProperties appProperties;
    private VectorStore vectorStore;
    Logger logger = LoggerFactory.getLogger(RagServiceImpl.class);

    public RagServiceImpl(AppProperties appProperties , VectorStore vectorStore) {
        this.appProperties = appProperties;
        this.vectorStore = vectorStore;
    }
    @Override
    public SerachResultDto serachSimilarChunks(SerachRequestDto requestDto) {
        
      List<Document> matchedDocs =   retrivalRelevantDocuments(
                requestDto.getQuery(),
                requestDto.getSimilaritySearch(),
                requestDto.getDocumentId(),
                requestDto.getTopK()
        );
      
        List<CitationDto> citations = matchedDocs.stream()
                .map(doc->mapToCitation(doc))
                .toList();

        
        return SerachResultDto.builder()
                .matches(citations)
                .totalMatches(citations.size())
                .query(requestDto.getQuery())
                .build();
    }

    private CitationDto mapToCitation(Document doc) {
        Map<String, Object> metadata = doc.getMetadata();
        UUID id = null;

        if (metadata.get("documentId") != null) {
                  try {
            id = UUID.fromString((String) metadata.get("documentId"));
             }catch (IllegalArgumentException e){

                  }
        }
        Integer chunkIndex = null;
        
    }

    private List<Document> retrivalRelevantDocuments(@NotBlank(message = "Query could not be null") String query, Double similaritySearch, UUID documentId, Integer topK) {

        int effectiveTopk= (topK != null) ? topK : appProperties.getRagProperties().getTopK();
        double effectiveSimilarity =  (similaritySearch != null) ? similaritySearch.intValue() : appProperties.getRagProperties().getSimilarityThreshold();

        var serachRequest = SearchRequest.builder()
                .query(query)
                .topK(effectiveTopk);

        if(effectiveSimilarity > 0.0){
            serachRequest.similarityThreshold(effectiveSimilarity);
                }
        if(documentId != null){
            FilterExpressionBuilder builder = new FilterExpressionBuilder();
            serachRequest.filterExpression(builder.eq("documentId",documentId.toString()).build());
        }
        try {
            List<Document> documents  =vectorStore.similaritySearch(serachRequest.build());
            logger.info("Retrieved {} documents",documents.size());
            return documents;
        } catch (Exception ex) {
            logger.error("Similarity search failed",ex);
            return Collections.emptyList();
        }

    }
}
