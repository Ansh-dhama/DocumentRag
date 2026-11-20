package com.example.backend.DocumentRag.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="DocumentMetadata")
public class DocumentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String filename;
    @Column(nullable = false)
    private String contentType;
    private Long fileSize;
    private Integer totalPages;
    private Integer totalChunks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(nullable = true)
    private String error;

    @Builder.Default
    @Column(nullable = false ,updatable = false)
    private Instant creationAt = Instant.now();

    @UpdateTimestamp
    private Instant updateAt;

}
