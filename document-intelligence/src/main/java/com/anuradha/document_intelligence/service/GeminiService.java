
package com.anuradha.document_intelligence.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService(
            @Value("${GEMINI_API_KEY}") String apiKey) {

        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String generateSummary(String documentText) {

        String prompt = """
                You are an AI document analysis assistant.

                Analyze the following document and provide:

                1. A short and clear summary.
                2. Five important points.
                3. Key information from the document.
                4. Main topics discussed.

                Use simple and understandable English.

                Document Text:
                """ + documentText;

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.5-flash",
                        prompt,
                        null
                );

        return response.text();
    }
}
