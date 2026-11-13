
package com.anuradha.document_intelligence.service;

import com.anuradha.document_intelligence.entity.Document;
import com.anuradha.document_intelligence.repository.DocumentRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PdfTextExtractionService pdfTextExtractionService;
    private final GeminiService geminiService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDirectory;

    public DocumentService(
            DocumentRepository documentRepository,
            PdfTextExtractionService pdfTextExtractionService,
            GeminiService geminiService) {

        this.documentRepository = documentRepository;
        this.pdfTextExtractionService = pdfTextExtractionService;
        this.geminiService = geminiService;
    }

    public Document uploadDocument(
            MultipartFile file,
            String ownerEmail) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file.");
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        String lowerName = originalName.toLowerCase();

        if (!lowerName.endsWith(".pdf") &&
                !lowerName.endsWith(".docx")) {
            throw new IllegalArgumentException(
                    "Only PDF and DOCX files are allowed."
            );
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "File size must be below 10 MB."
            );
        }

        String storedName = UUID.randomUUID().toString()
                + (lowerName.endsWith(".pdf") ? ".pdf" : ".docx");

        Path uploadPath = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(uploadPath);

        Path destination = uploadPath.resolve(storedName).normalize();

        if (!destination.startsWith(uploadPath)) {
            throw new IllegalArgumentException("Invalid file path.");
        }

        Files.copy(file.getInputStream(), destination);

        Document document = new Document();
        document.setOriginalFileName(originalName);
        document.setStoredFileName(storedName);
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setOwnerEmail(ownerEmail);

        return documentRepository.save(document);
    }

    public List<Document> getUserDocuments(String ownerEmail) {
        return documentRepository.findByOwnerEmail(ownerEmail);
    }

    public String extractPdfText(Long documentId, String ownerEmail)
            throws IOException {

        Optional<Document> optionalDocument =
                documentRepository.findById(documentId);

        if (optionalDocument.isEmpty()) {
            throw new IllegalArgumentException("Document not found.");
        }

        Document document = optionalDocument.get();

        if (!document.getOwnerEmail().equals(ownerEmail)) {
            throw new IllegalArgumentException("Document not found.");
        }

        if (!document.getStoredFileName().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException(
                    "Only PDF text extraction is supported."
            );
        }

        Path filePath = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize()
                .resolve(document.getStoredFileName());

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("Uploaded PDF file not found.");
        }

        return pdfTextExtractionService.extractText(filePath);
    }

    // Generate AI summary using Gemini
    public String generateDocumentSummary(
            Long documentId,
            String ownerEmail) throws IOException {

        String extractedText = extractPdfText(documentId, ownerEmail);

        if (extractedText == null || extractedText.isBlank()) {
            throw new IllegalArgumentException(
                    "No readable text found in PDF."
            );
        }

        if (extractedText.length() > 30000) {
            extractedText = extractedText.substring(0, 30000);
        }

        return geminiService.generateSummary(extractedText);
    }
}
