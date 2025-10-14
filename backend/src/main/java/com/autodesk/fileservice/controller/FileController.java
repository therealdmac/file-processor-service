package com.autodesk.fileservice.controller;

import com.autodesk.fileservice.model.FileMetadata;
import com.autodesk.fileservice.service.FileProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final FileProcessingService service;

    public FileController(FileProcessingService service) {
        this.service = service;
    }

    @Operation(
        summary = "Upload a file",
        description = "Uploads a .txt or .csv file, counts lines and words, then stores the result",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "File to upload (.txt or .csv)",
            required = true,
            content = @Content(mediaType = "multipart/form-data")
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file type or error")
        }
    )
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "No file uploaded"));
        }

        FileMetadata meta = service.processAndSave(file);
        return ResponseEntity.ok(meta);
    }

    @Operation(
        summary = "List all uploaded files",
        description = "Retrieves metadata for all uploaded files, including filename, line count, word count, and upload timestamp.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of uploaded files",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = FileMetadata.class)
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @GetMapping("/list")
    public ResponseEntity<Page<FileMetadata>> getFiles(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        Page<FileMetadata> files = service.listAllPaginated(page, size);
        return ResponseEntity.ok(files);
    }

    @Operation(
        summary = "Get file metadata by ID",
        description = "Retrieves metadata details for a specific uploaded file using its unique ID.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved file metadata",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FileMetadata.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "File record not found",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> get(
            @Parameter(description = "Unique identifier of the file metadata record", example = "1")
            @PathVariable("id") Long id) {

        Optional<FileMetadata> fileOpt = service.findById(id);

        if (fileOpt.isPresent()) {
            return ResponseEntity.ok(fileOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Record not found"));
        }
    }
}
