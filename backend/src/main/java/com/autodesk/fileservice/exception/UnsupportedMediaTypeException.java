package com.autodesk.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class UnsupportedMediaTypeException extends FileValidationException {

    public static final String MESSAGE = "Unsupported file type. Only .txt and .csv are allowed.";
    public UnsupportedMediaTypeException() {
        super(MESSAGE);
    }
}
