package com.anuradha.document_intelligence.repository;

import com.anuradha.document_intelligence.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOwnerEmail(String ownerEmail);

}