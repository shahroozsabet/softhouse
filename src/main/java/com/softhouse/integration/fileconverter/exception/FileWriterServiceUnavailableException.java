package com.softhouse.integration.fileconverter.exception;

import java.io.Serial;

/**
 * Created by Shahrooz on 02/17/2022.
 */
public class FileWriterServiceUnavailableException extends RuntimeException {

    @Serial
    static final long serialVersionUID = -7562565149975981884L;

    public FileWriterServiceUnavailableException(String message) {
        super(message);
    }

    public FileWriterServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
