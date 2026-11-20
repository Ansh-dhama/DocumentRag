package com.example.backend.DocumentRag.Repo;

import com.example.backend.DocumentRag.Entity.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentMetadataRepo extends JpaRepository<DocumentMetadata, UUID> {

}
