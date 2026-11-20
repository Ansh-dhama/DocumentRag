package com.example.backend.DocumentRag.Controller;


import com.example.backend.DocumentRag.Dto.ApiResponse;
import com.example.backend.DocumentRag.Dto.DocumentResponseDto;
import com.example.backend.DocumentRag.Service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;



    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadDocument(@RequestParam("file") MultipartFile file) {
          DocumentResponseDto responseDto =  documentService.uploadAndProcess(file);
          return
                  ResponseEntity.status(HttpStatus.CREATED)
                          .body(ApiResponse.<DocumentResponseDto>builder()
                                  .success(true)
                                  .data(responseDto)
                                  .message("Document uploaded and indexed succesfully")
                                  .build()
                          ) ;
    }

}
