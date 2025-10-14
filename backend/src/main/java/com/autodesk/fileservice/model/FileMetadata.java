package com.autodesk.fileservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_metadata")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "line_count")
    private long lineCount;

    @Column(name = "word_count")
    private long wordCount;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    public FileMetadata() {}

    public FileMetadata(String fileName, long lineCount, long wordCount, LocalDateTime uploadedAt) {
        this.fileName = fileName;
        this.lineCount = lineCount;
        this.wordCount = wordCount;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public long getLineCount() { return lineCount; }
    public void setLineCount(long lineCount) { this.lineCount = lineCount; }
    public long getWordCount() { return wordCount; }
    public void setWordCount(long wordCount) { this.wordCount = wordCount; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
