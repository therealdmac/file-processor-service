package com.autodesk.fileservice.service;

import com.autodesk.fileservice.exception.PayloadTooLargeException;
import com.autodesk.fileservice.exception.UnsupportedMediaTypeException;
import com.autodesk.fileservice.model.FileMetadata;
import com.autodesk.fileservice.repository.FileMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileProcessingServiceTest {

    private FileMetadataRepository repository;
    private FileProcessingService service;

    @BeforeEach
    void setup() {
        repository = mock(FileMetadataRepository.class);
        service = new FileProcessingService(repository);
    }

    private MockMultipartFile mockFile(String name, String content) {
        return new MockMultipartFile("file", name, "text/plain", content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldAllowTxtAndCsvFilesOnly() {
        assertThat(service.isAllowedFile("notes.txt")).isTrue();
        assertThat(service.isAllowedFile("data.csv")).isTrue();
        assertThat(service.isAllowedFile("image.png")).isFalse();
        assertThat(service.isAllowedFile("report.exe")).isFalse();
        assertThat(service.isAllowedFile(null)).isFalse();
        assertThat(service.isAllowedFile("   ")).isFalse();
    }

    @Test
    void shouldProcessAndSaveFileSuccessfully() throws Exception {
        // given
        MockMultipartFile file = mockFile("test.txt", "hello world\nthis is a test");
        when(repository.save(any(FileMetadata.class)))
                .thenAnswer(invocation -> {
                    FileMetadata m = invocation.getArgument(0);
                    // simulate persisted entity with id
                    return new FileMetadata(m.getFileName(), m.getLineCount(), m.getWordCount(), LocalDateTime.now());
                });

        FileMetadata result = service.processAndSave(file);

        assertThat(result.getFileName()).isEqualTo("test.txt");
        assertThat(result.getLineCount()).isEqualTo(2);
        assertThat(result.getWordCount()).isEqualTo(6);

        ArgumentCaptor<FileMetadata> captor = ArgumentCaptor.forClass(FileMetadata.class);
        verify(repository).save(captor.capture());
        FileMetadata saved = captor.getValue();
        assertThat(saved.getLineCount()).isEqualTo(2);
        assertThat(saved.getWordCount()).isEqualTo(6);
    }

    @Test
    void shouldThrowWhenUnsupportedExtension() {
        MockMultipartFile bad = mockFile("virus.exe", "malicious content");
        assertThatThrownBy(() -> service.processAndSave(bad))
                .isInstanceOf(UnsupportedMediaTypeException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowWhenFileTooLarge() {
        byte[] content = new byte[(int) (5 * 1024 * 1024 + 1)]; // > 5MB
        MockMultipartFile bigFile = new MockMultipartFile("file", "large.txt", "text/plain", content);
        assertThatThrownBy(() -> service.processAndSave(bigFile))
                .isInstanceOf(PayloadTooLargeException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowWhenFileProcessingFails() throws Exception {
        MockMultipartFile brokenFile = spy(mockFile("broken.txt", "irrelevant"));
        when(brokenFile.getInputStream()).thenThrow(new java.io.IOException("IO error"));

        assertThatThrownBy(() -> service.processAndSave(brokenFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to process file");
    }

    @Test
    void shouldListAllFiles() {
        FileMetadata f = new FileMetadata("a.txt", 1, 1, LocalDateTime.now());
        when(repository.findAll()).thenReturn(List.of(f));
        assertThat(service.listAll()).containsExactly(f);
    }

    @Test
    void shouldFindById() {
        FileMetadata f = new FileMetadata("a.txt", 1, 1, LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(f));
        assertThat(service.findById(1L)).contains(f);
    }
}

