package com.example.backend.DocumentRag.ServiceImpl;

import com.example.backend.DocumentRag.Exception.DocumentException;
import com.example.backend.DocumentRag.Service.ParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;

import java.util.List;


@Service
public class parserServiceImpl implements ParserService {
    private final static Logger logger = LoggerFactory.getLogger(parserServiceImpl.class);
    @Override
    public List<Document> parse(MultipartFile file) {
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String contentName =   file.getContentType() != null ? file.getContentType() : "";

        logger.info("Parsing file {} , size  {} , contentType {}", fileName , file.getSize(), contentName);

        try{
            Resource resource = new ByteArrayResource(file.getBytes()){
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
            if(fileName.toLowerCase().endsWith(".pdf")  || contentName.contains(".pdf") ){
                return parsePdf(resource);
            }else{
                return parseGenericFile(resource);
            }

        } catch (Exception e){
            logger.error("Failed to read file bytes " + e.getMessage());
            throw new DocumentException(" Could not parse document" + e.getMessage());
        }
    }

    private List<Document> parsePdf(Resource resource) {
          PagePdfDocumentReader reader = new PagePdfDocumentReader(resource , PdfDocumentReaderConfig.builder()
                  .withPageTopMargin(0)
                  .withPageBottomMargin(0)
                  .build());
          return reader.get();
    }

    private List<Document> parseGenericFile(Resource resource) {
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        return reader.get();
    }
}
