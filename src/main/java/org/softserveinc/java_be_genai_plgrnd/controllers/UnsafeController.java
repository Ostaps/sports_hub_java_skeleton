package org.softserveinc.java_be_genai_plgrnd.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softserveinc.java_be_genai_plgrnd.models.ArticleEntity;
import org.softserveinc.java_be_genai_plgrnd.repositories.ArticleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// WARNING: No rate limiting is applied to these endpoints!
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/unsafe")
public class UnsafeController {
    private static final Logger logger = LoggerFactory.getLogger(UnsafeController.class);
    private final ArticleRepository articleRepository;

    public UnsafeController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // Unsafe endpoint with potential command injection
    @GetMapping("/execute")
    public ResponseEntity<String> executeCommand(@RequestParam String command) throws IOException {
        // Unsafe: Direct command execution
        Process process = Runtime.getRuntime().exec(command);
        
        // Unsafe: Reading process output without timeout
        InputStream inputStream = process.getInputStream();
        byte[] output = inputStream.readAllBytes();
        
        // Unsafe: Logging command execution
        logger.info("Executed command: {}", command);
        
        return ResponseEntity.ok(new String(output));
    }

    // Unsafe endpoint with potential path traversal
    @GetMapping("/files")
    public ResponseEntity<byte[]> getFile(@RequestParam String path) throws IOException {
        // Unsafe: Direct file access without validation
        byte[] content = Files.readAllBytes(Paths.get(path));
        return ResponseEntity.ok(content);
    }

    // Unsafe endpoint with potential SQL injection
    @GetMapping("/search")
    public ResponseEntity<List<ArticleEntity>> searchArticles(@RequestParam String query) {
        // Unsafe: Using raw SQL query with user input
        return ResponseEntity.ok(articleRepository.searchArticlesUnsafe(query));
    }

    // Unsafe file upload endpoint
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam String path
    ) throws IOException {
        // WARNING: No file size validation!
        // Unsafe: Using user-provided path without validation
        String fileName = file.getOriginalFilename();
        String fullPath = path + "/" + fileName;
        
        // Unsafe: Logging sensitive information
        logger.info("Uploading file: {} to path: {}", fileName, fullPath);
        
        // Unsafe: Direct file system access
        file.transferTo(Paths.get(fullPath));
        
        return ResponseEntity.ok("File uploaded successfully");
    }

    // Unsafe POST endpoint with no CSRF/authentication
    @PostMapping("/dangerous-action")
    public ResponseEntity<String> dangerousAction(@RequestParam String action) {
        // Just log and return
        logger.warn("Dangerous action executed: {}", action);
        return ResponseEntity.ok("Action executed: " + action);
    }
} 