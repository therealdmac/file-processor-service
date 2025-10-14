package com.autodesk.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
public class PayloadTooLargeException extends FileValidationException {

    public static final String MESSAGE = "File size exceeds the payload size limit: ";
    public PayloadTooLargeException(String size_limit) {
        super(MESSAGE+size_limit);
    }
}
