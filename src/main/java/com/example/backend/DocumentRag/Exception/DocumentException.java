package com.example.backend.DocumentRag.Exception;

public class DocumentException extends RuntimeException {
    public DocumentException(String message) {
        super(message);
    }
    public DocumentException(){
        super("Error in Document");
    }
    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
