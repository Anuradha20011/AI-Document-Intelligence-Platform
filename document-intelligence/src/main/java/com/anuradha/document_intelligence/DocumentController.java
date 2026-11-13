
package com.anuradha.document_intelligence;

import com.anuradha.document_intelligence.entity.Document;
import com.anuradha.document_intelligence.service.DocumentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // Upload PDF or DOCX
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            Document document = documentService.uploadDocument(
                    file,
                    authentication.getName()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "Document uploaded successfully!",
                            "documentId", document.getId(),
                            "fileName", document.getOriginalFileName(),
                            "fileSize", document.getFileSize()
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("message", "File upload failed.")
            );
        }
    }

    // Get logged-in user's documents
    @GetMapping
    public ResponseEntity<List<Document>> getUserDocuments(
            Authentication authentication) {

        List<Document> documents =
                documentService.getUserDocuments(authentication.getName());

        return ResponseEntity.ok(documents);
    }

    // Extract text from uploaded PDF
    @GetMapping("/{documentId}/text")
    public ResponseEntity<?> extractPdfText(
            @PathVariable Long documentId,
            Authentication authentication) {

        try {
            String extractedText = documentService.extractPdfText(
                    documentId,
                    authentication.getName()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "documentId", documentId,
                            "extractedText", extractedText
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("message", "PDF text extraction failed.")
            );
        }
    }

    @GetMapping("/{documentId}/summary")
    public ResponseEntity<?> generateSummary(   
        @PathVariable Long documentId,
        Authentication authentication) {

        try {
            String summary = documentService.generateDocumentSummary(
                documentId,
                authentication.getName()
            );

            return ResponseEntity.ok(
                Map.of(
                    "documentId", documentId,
                    "summary", summary
                )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message", e.getMessage())
            );

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                Map.of("message", "AI summary generation failed.")
            );
        }
    }
}
