# AI Document Intelligence Platform

A backend application where users can register, log in, upload PDF/DOCX documents and get an AI-generated summary using Google Gemini.

## Features

- User registration and login with JWT authentication (Spring Security, BCrypt)
- Upload PDF and DOCX files (10 MB limit)
- User-specific document listing with ownership checks
- Text extraction from PDF using Apache PDFBox
- AI summary of a document using the Gemini API (short summary, five important points, key information, main topics)

## Tech Stack

- Java 17, Spring Boot
- Spring Security + JWT
- Spring Data JPA (Hibernate), MySQL 8
- Apache PDFBox
- Google Gemini API (google-genai SDK)
- Maven

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Create a new account |
| POST | /api/auth/login | Log in and get a JWT token |
| POST | /api/documents/upload | Upload a PDF or DOCX (form field: file) |
| GET | /api/documents/{documentId}/text | Get extracted text |
| GET | /api/documents/{documentId}/summary | Get AI summary |

All /api/documents endpoints need the header `Authorization: Bearer <token>`.

## Setup

1. Install JDK 17 and MySQL 8.
2. Set these environment variables (do not commit them to Git):
   - `JWT_SECRET` - secret used to sign JWT tokens
   - `GEMINI_API_KEY` - API key from Google AI Studio
   - `DB_PASSWORD` - your MySQL root password
3. The database `document_intelligence` is created automatically on first run.
4. Start the backend from the `document-intelligence` folder: 
.\mvnw.cmd spring-boot:run

The server runs at http://localhost:8080

## Project Status

- Backend: authentication, document upload, PDF text extraction and Gemini summary are working.
- Frontend (React): planned

## Author

Anuradha Kumari - B.Tech Computer Science and Engineering student