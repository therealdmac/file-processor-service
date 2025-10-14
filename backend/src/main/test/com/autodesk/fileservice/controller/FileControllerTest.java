package com.autodesk.fileservice.controller;

import com.autodesk.fileservice.exception.PayloadTooLargeException;
import com.autodesk.fileservice.exception.UnsupportedMediaTypeException;
import com.autodesk.fileservice.model.FileMetadata;
import com.autodesk.fileservice.service.FileProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileProcessingService fileProcessingService;

    private MockMultipartFile validTxtFile;
    private MockMultipartFile largeFile;
    private MockMultipartFile invalidFile;

    @BeforeEach
    void setup() {
        validTxtFile = new MockMultipartFile(
                "file", "test.txt", "text/plain",
                "Hello world\nThis is a test".getBytes(StandardCharsets.UTF_8)
        );

        largeFile = new MockMultipartFile(
                "file", "bigfile.txt", "text/plain",
                new byte[6 * 1024 * 1024] // 6 MB
        );

        invalidFile = new MockMultipartFile(
                "file", "test.exe", "application/octet-stream",
                "malicious content".getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void testUpload_ValidTxtFile_ReturnsOk() throws Exception {
        FileMetadata metadata = new FileMetadata();
        metadata.setId(1L);
        metadata.setFileName("test.txt");
        metadata.setLineCount(2);
        metadata.setWordCount(5);

        when(fileProcessingService.isAllowedFile(any())).thenReturn(true);
        when(fileProcessingService.processAndSave(any())).thenReturn(metadata);

        mockMvc.perform(multipart("/api/files/upload")
                        .file(validTxtFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andExpect(jsonPath("$.lineCount").value(2))
                .andExpect(jsonPath("$.wordCount").value(5));
    }

    @Test
    void testUpload_InvalidFileType_ReturnsBadRequest() throws Exception {
        when(fileProcessingService.isAllowedFile(any())).thenReturn(false);
        when(fileProcessingService.processAndSave(any(MultipartFile.class)))
                .thenThrow(new UnsupportedMediaTypeException());

        mockMvc.perform(multipart("/api/files/upload")
                        .file(invalidFile))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error").value(UnsupportedMediaTypeException.MESSAGE));
    }

    @Test
    void testUpload_FileTooLarge_ReturnsBadRequest() throws Exception {
        when(fileProcessingService.isAllowedFile(any())).thenReturn(true);
        when(fileProcessingService.processAndSave(any(MultipartFile.class)))
                .thenThrow(new PayloadTooLargeException("5MB"));

        mockMvc.perform(multipart("/api/files/upload")
                        .file(largeFile))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.error").value(containsString(PayloadTooLargeException.MESSAGE)));
    }

    @Test
    void testUpload_InternalError_Returns500() throws Exception {
        when(fileProcessingService.isAllowedFile(any())).thenReturn(true);
        when(fileProcessingService.processAndSave(any())).thenThrow(new RuntimeException("Disk full"));

        mockMvc.perform(multipart("/api/files/upload")
                        .file(validTxtFile))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }
}

