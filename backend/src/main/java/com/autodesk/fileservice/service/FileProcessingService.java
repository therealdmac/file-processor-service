package com.autodesk.fileservice.service;

import com.autodesk.fileservice.exception.PayloadTooLargeException;
import com.autodesk.fileservice.exception.UnsupportedMediaTypeException;
import com.autodesk.fileservice.model.FileMetadata;
import com.autodesk.fileservice.repository.FileMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class FileProcessingService {
    private static final Logger log = LoggerFactory.getLogger(FileProcessingService.class);
    private final FileMetadataRepository repository;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public FileProcessingService(FileMetadataRepository repository) {
        this.repository = repository;
    }

    public boolean isAllowedFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".txt") || lower.endsWith(".csv");
    }

    public FileMetadata processAndSave(MultipartFile file) {
        Objects.requireNonNull(file, "file must not be null");

        String filename = file.getOriginalFilename();
        if (!isAllowedFile(filename)) {
            throw new UnsupportedMediaTypeException();
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new PayloadTooLargeException(MAX_FILE_SIZE+"b");
        }

        log.info("Starting processing for file: {}", filename);
        long lineCount = 0L;
        long wordCount = 0L;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                if (!line.isBlank()) {
                    String[] words = line.trim().split("\\s+");
                    wordCount += words.length;
                }
            }
        } catch (IOException ex) {
            log.error("Error while processing file: {}", filename, ex);
            throw new RuntimeException("Failed to process file");
        }

        FileMetadata meta = new FileMetadata(filename, lineCount, wordCount, LocalDateTime.now());
        FileMetadata saved = repository.save(meta);
        log.info("Completed processing for file: {} -> lines: {}, words: {}, id: {}", filename, lineCount, wordCount, saved.getId());
        return saved;
    }

    public List<FileMetadata> listAll() {
        return repository.findAll();
    }

    public Page<FileMetadata> listAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedAt").descending());
        return repository.findAll(pageable);
    }

    public java.util.Optional<FileMetadata> findById(Long id) {
        return repository.findById(id);
    }
}
